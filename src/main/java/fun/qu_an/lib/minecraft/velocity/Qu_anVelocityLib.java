package fun.qu_an.lib.minecraft.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class Qu_anVelocityLib { // 先这么用，，以后再挪出去
	private static final Object libPlugin = VelocityChatPlugin.getInstance();
	private static final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();

	/**
	 * 获取群组服务器API
	 *
	 * @return 群组服务器API
	 */
	public static ProxyServer getProxyServer() {
		return proxyServer;
	}

	/**
	 * 获取库的插件实例
	 *
	 * @return 库的插件实例
	 */
	public static Object getLibPlugin() {
		return libPlugin;
	}
}
