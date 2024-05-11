package xyz.fcidd.lib.velocity.util;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PlayerUtil {

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull PlayerUtil create(ProxyServer proxyServer, Object plugin) {
		return new PlayerUtil(proxyServer, plugin);
	}

	private final Object plugin;
	private final ProxyServer proxyServer;
	private volatile PermissionsSetupHandler permissionsSetupHandler;

	PlayerUtil(ProxyServer proxyServer, Object plugin) {
		this.proxyServer = proxyServer;
		this.plugin = plugin;
	}

	/**
	 * 将指定的玩家切换至目标玩家所在的服务器后执行传送指令
	 *
	 * @param sourcePlayer     指定的玩家
	 * @param targetPlayerName 目标玩家
	 * @return 目标玩家不存在、与指定玩家处于同一服务器或不处于任何服务器时返回 false<p>
	 * 只要服务器切换成功，无论是否成功执行传送指令都会返回 true
	 */
	public boolean tpWithServerSwitch(@NotNull Player sourcePlayer, @NotNull String targetPlayerName) {
		Optional<Player> optional = proxyServer.getPlayer(targetPlayerName);
		if (optional.isEmpty()) return false;
		if (switchToTargetPlayer(sourcePlayer, optional.get())) {
			// 运行指令以tp
			proxyServer.getCommandManager().executeImmediatelyAsync(sourcePlayer, "/tp " + targetPlayerName);
			return true;
		}
		return false;
	}

	/**
	 * 将指定的玩家切换至目标玩家所在的服务器
	 *
	 * @param sourcePlayer 指定的玩家
	 * @param targetPlayer 目标玩家
	 * @return 目标玩家与指定玩家处于同一服务器或不处于任何服务器时返回 false<p>
	 * 服务器切换成功则返回 true
	 */
	public boolean switchToTargetPlayer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
		Optional<RegisteredServer> targetServerOptional = targetPlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (targetServerOptional.isPresent()) {
			RegisteredServer targetServer = targetServerOptional.get();
			Optional<RegisteredServer> sourceServerOptional = sourcePlayer
				.getCurrentServer()
				.map(ServerConnection::getServer);
			if (sourceServerOptional.isEmpty()
				|| !sourceServerOptional.get().equals(targetServer)) {
				// 来源为空或与目标不相等，则需要传送玩家
				return sourcePlayer
					.createConnectionRequest(targetServer)
					.connect()
					.join()
					.isSuccessful();
			}
		}
		return false;
	}

	/**
	 * 判断两个玩家是否处于同一个服务器
	 *
	 * @param player1 玩家1
	 * @param player2 玩家2
	 * @return 任一玩家不处于任何服务器、两玩家不处于同一服务器时返回 false<p>
	 * 两个玩家处于同一个服务器时返回 true
	 */
	public boolean hasTheSameServer(@NotNull Player player1, @NotNull Player player2) {
		Optional<RegisteredServer> sourceServerOptional = player1
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (sourceServerOptional.isEmpty()) return false;
		Optional<RegisteredServer> targetServerOptional = player2
			.getCurrentServer()
			.map(ServerConnection::getServer);
		return sourceServerOptional.equals(targetServerOptional);
	}

	/**
	 * 获取在线玩家样例数组
	 *
	 * @return 在线玩家样例数组
	 */
	public ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return getSamplePlayers(20);
	}

	/**
	 * 获取在线玩家样例数组
	 * @param limit 获取的玩家列表的数量上限
	 * @return 在线玩家样例数组
	 */
	public ServerPing.SamplePlayer @NotNull [] getSamplePlayers(int limit) {
		return proxyServer.getAllPlayers()
			.stream()
			.limit(limit)
			.map(this::toSamplePlayer)
			.toArray(ServerPing.SamplePlayer[]::new);
	}

	/**
	 * 根据玩家实例创建样例玩家
	 *
	 * @param player 玩家实例
	 * @return 样例玩家
	 */
	@Contract("_ -> new")
	public ServerPing.@NotNull SamplePlayer toSamplePlayer(@NotNull Player player) {
		return new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId());
	}

	/**
	 * 获取在线玩家的玩家名列表
	 *
	 * @return 在线玩家的玩家名列表
	 */
	public @NotNull List<String> getPlayerNames() {
		return proxyServer
			.getAllPlayers()
			.stream()
			.map(Player::getUsername)
			.toList();
	}

	public @NotNull Component getPlayerComponent(@NotNull Player player) {
		String playerName = player.getUsername();
		return Component.text(playerName)
			.hoverEvent(player.asHoverEvent())
			.clickEvent(ClickEvent.clickEvent(
				ClickEvent.Action.SUGGEST_COMMAND,
				"/tell " + playerName + " "
			));
	}

	public void registerPermission(String permissionKey, Predicate<Player> playerPredicate) {
		if (permissionsSetupHandler == null) {
			synchronized (this) {
				if (permissionsSetupHandler == null) {
					permissionsSetupHandler = new PermissionsSetupHandler();
					proxyServer.getEventManager().register(plugin, permissionsSetupHandler);
				}
			}
		}
		permissionsSetupHandler.permissions.put(permissionKey, playerPredicate);
	}

	public @Nullable Predicate<Player> unregisterPermission(String permissionKey) {
		if (permissionsSetupHandler == null) {
			return null;
		}
		return permissionsSetupHandler.permissions.remove(Objects.requireNonNull(permissionKey));
	}

	private static class PermissionsSetupHandler {
		private final Map<String, Predicate<Player>> permissions = new ConcurrentHashMap<>();

		@Subscribe
		public void onPermissionInit(@NotNull PermissionsSetupEvent event) {
			if (!(event.getSubject() instanceof Player player)) return;
			// 获取原权限函数
			PermissionFunction function = event.createFunction(player);
			// 设置新权限函数
			event.setProvider(subject -> permission -> {
				Predicate<Player> p = permissions.get(permission);
				if (p == null) { // 新函数继承原函数
					return function.getPermissionValue(permission);
				}
				return Tristate.fromBoolean(p.test(player));
			});
		}
	}
}
