package xyz.fcidd.velocity.chat.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.fcidd.lib.velocity.language.LanguageManager;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.command.Commands;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.Utils;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;
import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;

public class Components {
	public static final Component DASH_AND_SPACE = Component.text("- ");

	public static final TranslatableComponent PLAYER_NOT_FOUND = Component.translatable("qu_an.chat.message.player_not_found");
	public static final TranslatableComponent SERVER_NOT_FOUND = Component.translatable("qu_an.chat.message.server_not_found");

	// 连接消息

	public static final TranslatableComponent CONNECTED = Component.translatable("qu_an.chat.message.connected");
	public static final TranslatableComponent SERVER_SWITCH = Component.translatable("qu_an.chat.message.server_switch");
	public static final TranslatableComponent DISCONNECT = Component.translatable("qu_an.chat.message.disconnect");
	public static final TranslatableComponent HAS_NO_SERVER = Component.translatable("qu_an.chat.player.has_no_server");

	// 消息通知

	public static final Component PROXY = Component.text("§4[Proxy]§r")
		.clickEvent(ClickEvent.suggestCommand("/" + Commands.TELLCONSOLE + " "));
	public static final Component PROXY_BROADCAST_PREFIX = PROXY.appendSpace();
	public static final Component PROXY_NOTIFY_0 = Component.text("→[");
	public static final Component PROXY_NOTIFY_1 = Component.text("§r] ");
	public static final TranslatableComponent DEFAULT_CHAT = Component.translatable("qu_an.chat.message.chat.default");
	public static final TranslatableComponent TELL_MESSAGE = Component.translatable("qu_an.chat.command.tell");
	public static final TranslatableComponent TELL_SUCCEED = Component.translatable("qu_an.chat.command.tell.succeed");
	public static final TranslatableComponent RELOADED = Component.translatable("qu_an.chat.command.reload");
	public static final TranslatableComponent CHANNEL_CURRENT = Component.translatable("qu_an.chat.command.channel.current");
	public static final TranslatableComponent CHANNEL_SWITCH = Component.translatable("qu_an.chat.command.channel.switch");
	public static final TranslatableComponent CHANNEL_GLOBAL = Component.translatable("qu_an.chat.command.global");
	public static final TranslatableComponent CHANNEL_LOCAL = Component.translatable("qu_an.chat.command.local");
	public static final TranslatableComponent HELP = Component.translatable("qu_an.chat.command.help");
	public static final TranslatableComponent HELP_FORMATTABLE_CHAT = Component.translatable("qu_an.chat.command.help.formattable_chat");
	public static final TranslatableComponent HELP_RELOAD = Component.translatable("qu_an.chat.command.help.reload");
	public static final TranslatableComponent HELP_GLOBAL = Component.translatable("qu_an.chat.command.help.player.global");
	public static final TranslatableComponent HELP_LOCAL = Component.translatable("qu_an.chat.command.help.player.local");
	public static final TranslatableComponent HELP_CHANNEL = Component.translatable("qu_an.chat.command.help.player.channel");
	public static final TranslatableComponent HELP_CHANNEL_GLOBAL = Component.translatable("qu_an.chat.command.help.player.channel.global");
	public static final TranslatableComponent HELP_CHANNEL_LOCAL = Component.translatable("qu_an.chat.command.help.player.channel.local");
	public static final TranslatableComponent HELP_BROADCAST = Component.translatable("qu_an.chat.command.help.console.broadcast");
	public static final TranslatableComponent HELP_NOTIFY = Component.translatable("qu_an.chat.command.help.console.notify");
	public static final TranslatableComponent HELP_TELL = Component.translatable("qu_an.chat.command.help.console.tell");
	public static final TranslatableComponent HELP_TELLCONSOLE = Component.translatable("qu_an.chat.command.help.player.tellconsole");

	// 群组名

	public static final Component PROXY_NAME = Component.translatable("qu_an.chat.proxy.name");

	// vc自带

	public static final Component PLAYER_ONLY = Component.translatable("velocity.command.players-only");

	/* 需要实时补全的 */

	// 服务器聊天消息

	public static final String SERVER_CHAT = "qu_an.chat.message.chat.server.";

	// 服务器名

	public static final String SERVER_NAME = "qu_an.chat.server.name.";

	// TODO 弃用格式化代码，改用 MiniMessage
	public static final LanguageManager DEFAULT_LM = LanguageManager.create(
		VelocityChatPlugin.getInstance(),
		Key.key("qu_an", "chat_default"),
		DATA_DIRECTORY.resolve("langs/default"),
		"langs/default",
		true
	);
	public static final LanguageManager CUSTOM_LM = LanguageManager.create(
		VelocityChatPlugin.getInstance(),
		Key.key("qu_an", "chat_custom"),
		DATA_DIRECTORY.resolve("langs/custom"),
		"langs/custom"
	);
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
		if (server == null) return Component.empty();
		return getServerComponent0(server, null);
	}

	public static @NotNull Component getServerComponent(@Nullable RegisteredServer server, @NotNull String currentServerId) {
		if (server == null) return Component.empty();
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
		String serverTranslationKey = SERVER_NAME + serverId;
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
						.appendNewline()
						.append(playerCountComponent)));
		} else {
			serverComponent = serverComponent
				.clickEvent(ClickEvent.runCommand("/server " + serverId))
				.hoverEvent(HoverEvent
					.showText(Component
						.translatable("velocity.command.server-tooltip-offer-connect-server")
						.appendNewline()
						.append(playerCountComponent)));
		}

		Caches.setServerComponent(server, onlinePlayers, serverComponent);
		return serverComponent;
	}

	public static Component formattedMessage(String message) {
		return SIMPLE_MINI_MESSAGE.deserialize(message);
	}

	public static @NotNull Component getGlobalPlayerChatComponent(@NotNull Player player, @NotNull Component chatMessage, RegisteredServer currentServer, String serverId) {
		// 玩家名
		Component playerNameComponent = getPlayerComponent(player);
		// 构建并发送玩家消息
		String serverChatFormatTranslationKey = SERVER_CHAT + serverId;
		if (Utils.hasTranslation(serverChatFormatTranslationKey)) {
			return Component.translatable(
				serverChatFormatTranslationKey, // 追加子服务器id
				PROXY_NAME, // 群组名称
				getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			);
		} else {
			return DEFAULT_CHAT.args(
				PROXY_NAME, // 群组名称
				getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			);
		}
	}
}
