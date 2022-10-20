package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import fun.qu_an.minecraft.velocity.api.util.ApiPlayerUtil;
import fun.qu_an.minecraft.velocity.api.util.ApiTaskUtil;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class ApiUtils {
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final Qu_anVelocityApi QU_AN_API = Qu_anVelocityApi.create(PROXY_SERVER, VelocityChatPlugin.getInstance());
	public static final ApiPlayerUtil API_PLAYER_UTIL = QU_AN_API.getProxyUtil();
	public static final ApiTaskUtil API_TASK_UTIL = QU_AN_API.getTaskUtil();
}
