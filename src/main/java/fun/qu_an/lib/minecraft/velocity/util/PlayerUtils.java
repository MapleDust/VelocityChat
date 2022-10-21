package fun.qu_an.lib.minecraft.velocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import fun.qu_an.lib.minecraft.velocity.Qu_anVelocityLib;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static fun.qu_an.lib.minecraft.velocity.util.ProxyUtils.PROXY_SERVER;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerUtils {
	private static final ProxyServer proxyServer = Qu_anVelocityLib.getProxyServer();

	/**
	 * 将指定的玩家切换至目标玩家所在的服务器后执行传送指令
	 *
	 * @param sourcePlayer     指定的玩家
	 * @param targetPlayerName 目标玩家
	 * @return 目标玩家不存在、与指定玩家处于同一服务器或不处于任何服务器时返回 false<p>
	 * 只要服务器切换成功，无论是否成功执行传送指令都会返回 true
	 */
	public static boolean tpWithServerSwitch(@NotNull Player sourcePlayer, @NotNull String targetPlayerName) {
		Optional<Player> optional = getPlayerByName(targetPlayerName);
		if (optional.isEmpty()) return false;
		if (switchToTargetPlayer(sourcePlayer, optional.get())) {
			// 运行指令以tp
			PROXY_SERVER.getCommandManager().executeImmediatelyAsync(sourcePlayer, "/tp " + targetPlayerName);
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
	public static boolean switchToTargetPlayer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
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
	public static boolean hasTheSameServer(@NotNull Player player1, @NotNull Player player2) {
		Optional<RegisteredServer> sourceServerOptional = player1
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (sourceServerOptional.isEmpty()) return false;
		Optional<RegisteredServer> targetServerOptional = player2
			.getCurrentServer()
			.map(ServerConnection::getServer);
		return targetServerOptional.isPresent()
			&& sourceServerOptional.get().equals(targetServerOptional.get());
	}

	/**
	 * 根据玩家名获取玩家实例
	 *
	 * @param name 玩家名
	 * @return 玩家实例的可空 Optional
	 */
	public static @NotNull Optional<Player> getPlayerByName(String name) {
		for (Player player : proxyServer.getAllPlayers()) {
			if (player.getUsername().equalsIgnoreCase(name)) return Optional.of(player);
		}
		return Optional.empty();
	}

	/**
	 * 获取在线玩家样例数组
	 *
	 * @return 在线玩家样例数组
	 */
	public static ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return proxyServer.getAllPlayers()
			.stream()
			.map(PlayerUtils::toSamplePlayer)
			.toArray(ServerPing.SamplePlayer[]::new);
	}

	/**
	 * 根据玩家实例创建样例玩家
	 *
	 * @param player 玩家实例
	 * @return 样例玩家
	 */
	@Contract("_ -> new")
	public static ServerPing.@NotNull SamplePlayer toSamplePlayer(@NotNull Player player) {
		return new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId());
	}

	/**
	 * 获取在线玩家的玩家名列表
	 *
	 * @return 在线玩家的玩家名列表
	 */
	public static List<String> playerNames() {
		return PROXY_SERVER
			.getAllPlayers()
			.stream()
			.map(Player::getUsername)
			.toList();
	}
}
