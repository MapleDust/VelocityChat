package fun.qu_an.lib.minecraft.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class Qu_anVelocityLib { // 先这么用，，以后再挪出去
	private static final Object instance = VelocityChatPlugin.getInstance();
	private static final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();

	public static ProxyServer getProxyServer() {
		return proxyServer;
	}

	public static Object getInstance() {
		return instance;
	}
}
