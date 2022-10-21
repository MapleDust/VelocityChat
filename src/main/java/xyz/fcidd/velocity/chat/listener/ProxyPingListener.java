package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import fun.qu_an.lib.minecraft.velocity.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;

public class ProxyPingListener {
	@Subscribe
	public void onProxyPing(@NotNull ProxyPingEvent event) {
		if (CONFIG.isSendPlayersOnPing()) {
			event.setPing(event
				.getPing()
				.asBuilder()
				.clearSamplePlayers()
				.samplePlayers(PlayerUtils.getSamplePlayers())
				.build());
		}
	}
}
