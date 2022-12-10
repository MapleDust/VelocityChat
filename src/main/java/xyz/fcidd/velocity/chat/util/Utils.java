package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.lib.minecraft.velocity.api.util.PlayerUtil;
import fun.qu_an.lib.minecraft.velocity.api.util.TaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class Utils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerUtil PLAYER_UTIL = PlayerUtil.create(PROXY_SERVER, VelocityChatPlugin.getInstance());
	public static final TaskUtil TASK_UTIL = TaskUtil.create(VelocityChatPlugin.getInstance(), PROXY_SERVER);
}
