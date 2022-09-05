package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import xyz.fcidd.velocity.chat.VelocityChatLifecycle;

public class ProxyReloadListener {
	@Subscribe
	public void onProxyReload(ProxyReloadEvent event) {
		VelocityChatLifecycle.reload();
	}
}
