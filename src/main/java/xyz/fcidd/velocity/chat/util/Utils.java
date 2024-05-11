package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.lib.velocity.util.PlayerUtil;
import xyz.fcidd.lib.velocity.util.TaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.Translates;

public class Utils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerUtil PLAYER_UTIL = PlayerUtil.create(PROXY_SERVER, VelocityChatPlugin.getInstance());
	public static final TaskUtil TASK_UTIL = TaskUtil.create(VelocityChatPlugin.getInstance(), PROXY_SERVER);

	public static boolean hasTranslation(@NotNull String key) {
		return Translates.CUSTOM_LM.contains(key);
	}

	public static void sendGlobalPlayerChat(@NotNull Player player, @NotNull Component chatMessage) {
		player.getCurrentServer().ifPresentOrElse(
			serverConnection -> sendGlobalPlayerChat(player, chatMessage, serverConnection.getServer(), serverConnection.getServerInfo().getName()),
			() -> sendGlobalPlayerChat(player, chatMessage, null, ""));
	}

	public static void sendGlobalPlayerChat(@NotNull Player player, @NotNull Component chatMessage, RegisteredServer currentServer, String serverId) {
		PROXY_SERVER.sendMessage(getGlobalPlayerChatComponent(player, chatMessage, currentServer, serverId));
	}

	public static @NotNull Component getGlobalPlayerChatComponent(@NotNull Player player, @NotNull Component chatMessage, RegisteredServer currentServer, String serverId) {
		// 玩家名
		Component playerNameComponent = ComponentUtils.getPlayerComponent(player);
		// 构建并发送玩家消息
		String serverChatFormatTranslationKey = Translates.SERVER_CHAT + serverId;
		if (hasTranslation(serverChatFormatTranslationKey)) {
			return Component.translatable(
				serverChatFormatTranslationKey, // 追加子服务器id
				Translates.PROXY_NAME, // 群组名称
				ComponentUtils.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			);
		} else {
			return Translates.DEFAULT_CHAT.args(
				Translates.PROXY_NAME, // 群组名称
				ComponentUtils.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			);
		}
	}
}
