package fun.qu_an.lib.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Plugin(id = "qu_an_api",
	name = "Qu'an API",
	version = "0.0.0",
	authors = "Harvey_Husky"
)
public class Qu_anLibPlugin {
	private static ProxyServer proxyServer;
	private static Logger logger;
	private static Qu_anLibPlugin instance;

	public static ProxyServer getProxyServer() {
		return proxyServer;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Qu_anLibPlugin getInstance() {
		return instance;
	}

	@Inject
	public Qu_anLibPlugin(ProxyServer proxyServer, Logger logger) {
		Qu_anLibPlugin.proxyServer = proxyServer;
		Qu_anLibPlugin.logger = logger;
		Qu_anLibPlugin.instance = this;
	}
}
