package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.lib.velocity.util.PlayerUtil;
import xyz.fcidd.lib.velocity.util.TaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.Components;

public class Utils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerUtil PLAYER_UTIL = PlayerUtil.create(PROXY_SERVER, VelocityChatPlugin.getInstance());
	public static final TaskUtil TASK_UTIL = TaskUtil.create(VelocityChatPlugin.getInstance(), PROXY_SERVER);

	public static boolean hasTranslation(@NotNull String key) {
		return Components.CUSTOM_LM.contains(key);
	}

	public static void sendGlobalPlayerChat(@NotNull Player player, @NotNull Component chatMessage) {
		player.getCurrentServer().ifPresentOrElse(
			serverConnection -> sendGlobalPlayerChat(player, chatMessage, serverConnection.getServer(), serverConnection.getServerInfo().getName()),
			() -> sendGlobalPlayerChat(player, chatMessage, null, ""));
	}

	public static void sendGlobalPlayerChat(@NotNull Player player, @NotNull Component chatMessage, RegisteredServer currentServer, String serverId) {
		PROXY_SERVER.sendMessage(Components.getGlobalPlayerChatComponent(player, chatMessage, currentServer, serverId));
	}

}
