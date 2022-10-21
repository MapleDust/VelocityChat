package xyz.fcidd.velocity.chat.component;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Components {
	private static final Map<Player, Component> PLAYER_COMPONENT_CACHE = new HashMap<>();

	public static @NotNull Component getPlayerComponent(@NotNull Player player) {
		Component component = PLAYER_COMPONENT_CACHE.get(player);
		if (component != null) return component;
		String playerName = player.getUsername();
		Component playerComponent = Component.text(playerName)
			.hoverEvent(player.asHoverEvent())
			.clickEvent(ClickEvent.clickEvent(
				ClickEvent.Action.SUGGEST_COMMAND,
				"/tell " + playerName + " "
			));
		PLAYER_COMPONENT_CACHE.put(player, playerComponent);
		return playerComponent;
	}

	public static void removeFromCache(@NotNull Player player) {
		PLAYER_COMPONENT_CACHE.remove(player);
	}

	public static @NotNull Component getServerComponent(@Nullable RegisteredServer server) {
		if(server == null) return Component.empty();
		return getServerComponent0(server, null);
	}

	public static @NotNull Component getServerComponent(@Nullable RegisteredServer server, @NotNull String currentServerId){
		if(server == null) return Component.empty();
		return getServerComponent0(server, currentServerId);
	}

	private static @NotNull Component getServerComponent0(@NotNull RegisteredServer server, String currentServerId) {
		String serverId = server.getServerInfo().getName();
		Component serverComponent;

		TranslatableComponent playerCountComponent;
		int onlinePlayers = server.getPlayersConnected().size();
		if (onlinePlayers == 1) {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-player-online");
		} else {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-players-online");
		}
		playerCountComponent = playerCountComponent.args(Component.text(onlinePlayers));
		serverComponent = Component
			.translatable(Translates.SERVER_NAME + serverId);
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

	public static void resetCache() {
		PLAYER_COMPONENT_CACHE.clear();
	}
}
