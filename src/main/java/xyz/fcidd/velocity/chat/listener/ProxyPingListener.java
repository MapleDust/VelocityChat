package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import xyz.fcidd.velocity.chat.util.PluginUtil;

import static xyz.fcidd.velocity.chat.config.ConfigManager.CONFIG;

public class ProxyPingListener {
	@Subscribe
	public void onProxyPing(ProxyPingEvent event) {
		if (CONFIG.isSendPlayersOnPing()) {
			ServerPing serverPing = event.getPing()
					.asBuilder()
					.clearSamplePlayers()
					.samplePlayers(PluginUtil.getSamplePlayers())
					.build();
			event.setPing(serverPing);
		}
	}
}
