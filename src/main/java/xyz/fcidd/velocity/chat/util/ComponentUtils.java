package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.Translates;

import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;

@SuppressWarnings("unused")
public class ComponentUtils {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	public static final MiniMessage SIMPLE_MINI_MESSAGE = MiniMessage.builder()
		.tags(TagResolver.builder()
			.resolver(StandardTags.decorations())
			.resolver(StandardTags.reset())
			.resolver(StandardTags.color()).build()).build();

	public static @NotNull Component getPlayerComponent(@NotNull Player player) {
		Component component = Caches.getPlayerComponent(player);
		if (component != null) return component;
		String playerName = player.getUsername();
		Component playerComponent = PLAYER_UTIL.getPlayerComponent(player);
		Caches.setPlayerComponent(player, playerComponent);
		return playerComponent;
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

		int onlinePlayers = server.getPlayersConnected().size();

		// 查缓存
		Component cachedServerComponent = Caches.getServerComponent(server, onlinePlayers);
		if (cachedServerComponent != null) {
			return cachedServerComponent;
		}

		TranslatableComponent playerCountComponent;
		if (onlinePlayers == 1) {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-player-online");
		} else {
			playerCountComponent = Component.translatable("velocity.command.server-tooltip-players-online");
		}
		playerCountComponent = playerCountComponent.args(Component.text(onlinePlayers));

		Component serverComponent;
		String serverTranslationKey = Translates.SERVER_NAME + serverId;
		if (Utils.hasTranslation(serverTranslationKey)) {
			serverComponent = Component.translatable(serverTranslationKey);
		} else {
			serverComponent = Component.text(serverId);
		}

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

		Caches.setServerComponent(server, onlinePlayers, serverComponent);
		return serverComponent;
	}

	public static Component formattedMessage(String message) {
		return SIMPLE_MINI_MESSAGE.deserialize(message);
	}
}
