package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import org.jetbrains.annotations.NotNull;
import fun.qu_an.lib.velocity.util.PluginUtils;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;

public class ProxyPingListener {
	@Subscribe
	public void onProxyPing(@NotNull ProxyPingEvent event) {
		if (CONFIG.isSendPlayersOnPing()) {
			event.setPing(event
				.getPing()
				.asBuilder()
				.clearSamplePlayers()
				.samplePlayers(PluginUtils.getSamplePlayers())
				.build());
		}
	}
}
