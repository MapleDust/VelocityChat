package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.lib.minecraft.velocity.api.util.ApiUtils;
import fun.qu_an.lib.minecraft.velocity.api.util.PlayerUtil;
import fun.qu_an.lib.minecraft.velocity.api.util.TaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class Utils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerUtil PLAYER_UTIL = ApiUtils.createPlayerUtil(PROXY_SERVER);
	public static final TaskUtil TASK_UTIL = ApiUtils.createTaskUtil(VelocityChatPlugin.getInstance(), PROXY_SERVER);
}
