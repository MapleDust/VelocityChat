package xyz.fcidd.velocity.chat.component;

import fun.qu_an.minecraft.velocity.api.language.LanguageLoader;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;
import static xyz.fcidd.velocity.chat.util.ApiUtils.QU_AN_API;

public class Translates {
	// 私聊消息

	public static final TranslatableComponent TELL_MESSAGE = Component.translatable("qu_an.chat.message.tell");
	public static final TranslatableComponent TELL_RESPONSE = Component.translatable("qu_an.chat.command.tell.response");

	// 连接消息

	public static final TranslatableComponent CONNECTED = Component.translatable("qu_an.chat.message.connected");
	public static final TranslatableComponent SERVER_SWITCH = Component.translatable("qu_an.chat.message.server_switch");
	public static final TranslatableComponent DISCONNECT = Component.translatable("qu_an.chat.message.disconnect");

	// 聊天消息

	public static final TranslatableComponent DEFAULT_CHAT = Component.translatable("qu_an.chat.message.chat.default");

	// 群组名

	public static final Component PROXY_NAME = Component.translatable("qu_an.chat.proxy.name");

	/* 需要实时补全的 */

	// 服务器聊天消息

	public static final String SERVER_CHAT = "qu_an.chat.message.chat.server.";

	// 服务器名

	public static final String SERVER_NAME = "qu_an.chat.server.name.";

	public static final LanguageLoader LANGUAGE_LOADER = QU_AN_API.getLanguageLoader(
		Key.key("qu_an", "chat"),
		DATA_DIRECTORY.resolve("langs"), "langs"
	);
}
