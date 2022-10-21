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

	public static boolean hasTheSameServer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
		Optional<RegisteredServer> sourceServerOptional = sourcePlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (sourceServerOptional.isEmpty()) return false;
		Optional<RegisteredServer> targetServerOptional = targetPlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		return targetServerOptional.isPresent()
			&& sourceServerOptional.get().equals(targetServerOptional.get());
	}

	public static @NotNull Optional<Player> getPlayerByName(String name) {
		for (Player player : proxyServer.getAllPlayers()) {
			if (player.getUsername().equalsIgnoreCase(name)) return Optional.of(player);
		}
		return Optional.empty();
	}

	public static ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return (ServerPing.SamplePlayer[]) proxyServer
			.getAllPlayers()
			.stream()
			.map(PlayerUtils::toSamplePlayer)
			.toArray();
	}

	@Contract("_ -> new")
	public static ServerPing.@NotNull SamplePlayer toSamplePlayer(@NotNull Player player) {
		return new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId());
	}

	/**
	 * 获取所有在线玩家的玩家名列表
	 */
	public static List<String> playerNames() {
		return PROXY_SERVER
			.getAllPlayers()
			.stream()
			.map(Player::getUsername)
			.toList();
	}
}
