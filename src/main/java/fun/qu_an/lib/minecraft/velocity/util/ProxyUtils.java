package fun.qu_an.lib.minecraft.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.lib.minecraft.velocity.Qu_anVelocityLib;

@SuppressWarnings("unused")
public class ProxyUtils {
	/**
	 * 群组服务器API
	 */
	public static final ProxyServer PROXY_SERVER = Qu_anVelocityLib.getProxyServer();

	/**
	 * 判断服务器是否处于在线模式
	 */
	public static boolean isOnlineMode() {
		return PROXY_SERVER.getConfiguration().isOnlineMode();
	}
}
