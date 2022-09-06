package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.util.FutureUtil;
import xyz.fcidd.velocity.chat.util.PlayerUtil;

public class PlayerDisconnectListener {
	private final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();

	@Subscribe
	public void onPlayerDisconnectAsync(DisconnectEvent event) {
		FutureUtil.thenRun(() -> onPlayerDisconnect(event));
	}

	public void onPlayerDisconnect(DisconnectEvent event) {
		Player player = event.getPlayer();
		// 玩家名
		Component playerNameComponent = PlayerUtil.getPlayerComponent(player);
		// 将玩家退出群组的消息发送给所有人
		proxyServer.getAllServers().forEach(server -> server.sendMessage(
				Component.text("§8[§c-§8]§r ")
						.append(playerNameComponent)
						.append(Component
								.text(" §2离开了群组"))
		));
		PlayerUtil.removeFromCache(player);
	}
}
