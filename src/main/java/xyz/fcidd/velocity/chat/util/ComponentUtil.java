package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import xyz.fcidd.velocity.chat.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

import static xyz.fcidd.velocity.chat.config.ConfigManager.CONFIG;

public class ComponentUtil {
	private static final Map<String, Component> PLAYER_COMPONENT_CACHE = new HashMap<>();
	private static final Component EMPTY_SERVER_CHAT_COMPONENT = Component.text("§8[ ]§r");

	public static Component getPlayerComponent(Player player) {
		String playerName = player.getUsername();
		if (PLAYER_COMPONENT_CACHE.containsKey(playerName)) {
			return PLAYER_COMPONENT_CACHE.get(playerName);
		}
		Component playerComponent = Component.text(playerName)
				.hoverEvent(player.asHoverEvent())
				.clickEvent(ClickEvent.clickEvent(
						ClickEvent.Action.SUGGEST_COMMAND,
						"/tell " + playerName + " "
				));
		PLAYER_COMPONENT_CACHE.put(playerName, playerComponent);
		return playerComponent;
	}

	public static void removeFromCache(Player player) {
		removeFromCache(player.getUsername());
	}

	public static void removeFromCache(String playerName) {
		PLAYER_COMPONENT_CACHE.remove(playerName);
	}


	public static Component getServerComponent(RegisteredServer server) {
		return getServerComponent(server, null);
	}

	public static Component getServerComponent(RegisteredServer server, String currentServerId) {
		String serverId = server.getServerInfo().getName();
		String serverName = CONFIG.getServerName(serverId);
		TranslatableComponent playerCountComponent;
		int onlinePlayers = server.getPlayersConnected().size();
		if (onlinePlayers == 1) {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-player-online");
		} else {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-players-online");
		}
		playerCountComponent = playerCountComponent.args(Component.text(onlinePlayers));
		Component serverComponent = Component.text(serverName);
		if (serverId.equals(currentServerId)) {
			serverComponent = serverComponent
					.hoverEvent(HoverEvent
							.showText(Component
									.translatable("velocity.command.server-tooltip-current-server")
									.append(Component.newline())
									.append(playerCountComponent)));
		} else {
			serverComponent = serverComponent
					.clickEvent(ClickEvent.runCommand("/server " + serverId))
					.hoverEvent(HoverEvent
							.showText(Component
									.translatable("velocity.command.server-tooltip-offer-connect-server")
									.append(Component.newline())
									.append(playerCountComponent)));
		}
		return serverComponent;
	}
}
