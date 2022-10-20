package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static xyz.fcidd.velocity.chat.util.ApiUtils.API_PLAYER_UTIL;
import static xyz.fcidd.velocity.chat.util.ApiUtils.PROXY_SERVER;

public class PlayerUtils {
	public static boolean tpWithServerSwitch(@NotNull CommandExecuteEvent event, @NotNull Player sourcePlayer, @NotNull String targetPlayerName) {
		Optional<Player> optional = API_PLAYER_UTIL.getPlayerByName(targetPlayerName);
		if (optional.isEmpty()) return false;
		if (switchToTargetPlayer(sourcePlayer, optional.get())) {
			// 运行指令以tp
			PROXY_SERVER.getCommandManager().executeImmediatelyAsync(sourcePlayer, event.getCommand());
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
}
