package xyz.fcidd.velocity.chat.message;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import xyz.fcidd.lib.velocity.language.LanguageManager;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.command.Commands;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

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
	public static final Component PROXY_NOTIFY_0 =Component.text("→[");
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
	public static final TranslatableComponent HELP_GLOBAL = Component.translatable("qu_an.chat.command.help.global");
	public static final TranslatableComponent HELP_LOCAL = Component.translatable("qu_an.chat.command.help.local");
	public static final TranslatableComponent HELP_CHANNEL = Component.translatable("qu_an.chat.command.help.channel");
	public static final TranslatableComponent HELP_CHANNEL_GLOBAL = Component.translatable("qu_an.chat.command.help.channel.global");
	public static final TranslatableComponent HELP_CHANNEL_LOCAL = Component.translatable("qu_an.chat.command.help.channel.local");
	public static final TranslatableComponent HELP_BROADCAST = Component.translatable("qu_an.chat.command.help.broadcast");
	public static final TranslatableComponent HELP_NOTIFY = Component.translatable("qu_an.chat.command.help.notify");
	public static final TranslatableComponent HELP_TELL = Component.translatable("qu_an.chat.command.help.tell");
	public static final TranslatableComponent HELP_TELLCONSOLE = Component.translatable("qu_an.chat.command.help.tellconsole");

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
}
