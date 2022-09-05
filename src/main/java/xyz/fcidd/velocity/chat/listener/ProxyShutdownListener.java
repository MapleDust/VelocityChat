package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import xyz.fcidd.velocity.chat.VelocityChatLifecycle;

public class ProxyShutdownListener {
	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		VelocityChatLifecycle.shutdown();
	}
}
