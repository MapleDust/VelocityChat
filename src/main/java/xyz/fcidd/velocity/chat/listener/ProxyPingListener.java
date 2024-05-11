package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.jetbrains.annotations.NotNull;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class ProxyPingListener {
	@Subscribe
	public void onProxyPing(@NotNull ProxyPingEvent event) {
		if (!CONFIG.isSendPlayersOnPing()) {
			return;
		}
		event.setPing(event
			.getPing()
			.asBuilder()
			.clearSamplePlayers()
			.samplePlayers(PROXY_SERVER
				.getAllPlayers()
				.stream()
				.map(player -> new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId()))
				.limit(10) // TODO 这里之前会发送所有玩家，并不需要那么多且易被用于攻击，需要限制一下。。
				.toArray(ServerPing.SamplePlayer[]::new))
			.build());
	}
}
