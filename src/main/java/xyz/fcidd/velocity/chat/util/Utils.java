package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.qu_an.lib.minecraft.velocity.api.util.PlayerUtil;
import fun.qu_an.lib.minecraft.velocity.api.util.TaskUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.text.Translates;

public class Utils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerUtil PLAYER_UTIL = PlayerUtil.create(PROXY_SERVER, VelocityChatPlugin.getInstance());
	public static final TaskUtil TASK_UTIL = TaskUtil.create(VelocityChatPlugin.getInstance(), PROXY_SERVER);

	public static boolean hasTranslation(String key) {
		return Translates.CUSTOM_LANG.contains(key);
	}

	public static void sendGlobalPlayerChat(Player player, String playerMessage) {
		player.getCurrentServer().ifPresentOrElse(
			currentServer -> sendGlobalPlayerChat(player, playerMessage, currentServer.getServer(), currentServer.getServerInfo().getName()),
			() -> sendGlobalPlayerChat(player, playerMessage, null, ""));
	}

	public static void sendGlobalPlayerChat(Player player, String playerMessage, RegisteredServer currentServer, String serverId) {
		// 玩家名
		Component playerNameComponent = ComponentUtils.getPlayerComponent(player);
		// 构建玩家消息，Velocity API 居然把玩家队伍颜色阻断掉了，导致不能显示玩家队伍颜色
		TextComponent chatMessage = Component.text(playerMessage);
		// 发送消息
		String serverChatFormatTranslationKey = Translates.SERVER_CHAT + serverId;
		if (hasTranslation(serverChatFormatTranslationKey)) {
			PROXY_SERVER.sendMessage(Component.translatable(
				serverChatFormatTranslationKey, // 追加子服务器id
				Translates.PROXY_NAME, // 群组名称
				ComponentUtils.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			));
		} else {
			PROXY_SERVER.sendMessage(Translates.DEFAULT_CHAT.args(
				Translates.PROXY_NAME, // 群组名称
				ComponentUtils.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			));
		}
	}
}
