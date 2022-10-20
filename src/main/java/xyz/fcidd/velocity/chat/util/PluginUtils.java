package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import fun.qu_an.minecraft.velocity.api.util.ProxyUtil;
import fun.qu_an.minecraft.velocity.api.util.TaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class PluginUtils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final Object PLUGIN = VelocityChatPlugin.getInstance();
	public static final Qu_anVelocityApi QU_AN_API = Qu_anVelocityApi.create(PROXY_SERVER, PLUGIN);
	public static final ProxyUtil PROXY_UTIL = QU_AN_API.getProxyUtil();
	public static final TaskUtil TASK_UTIL = QU_AN_API.getTaskUtil();
}
