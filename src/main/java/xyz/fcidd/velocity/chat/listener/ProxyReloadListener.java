package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import xyz.fcidd.velocity.chat.config.ConfigManager;

import java.util.ConcurrentModificationException;

import static xyz.fcidd.velocity.chat.util.PluginUtil.LOGGER;

public class ProxyReloadListener {
	@Subscribe
	public void onProxyReload(ProxyReloadEvent event) {
		try {
			ConfigManager.load();
			LOGGER.info("VelocityChat重载成功！");
		} catch (ConcurrentModificationException e) {
			LOGGER.warn("VelocityChat重载间隔过短，请稍后再试！");
		}
	}
}
