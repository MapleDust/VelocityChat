package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class PlayerDisconnectListener {
	private final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();

	@Subscribe
	public EventTask onPlayerDisconnectAsync(DisconnectEvent event) {
		return EventTask.async(() -> onPlayerDisconnect(event));
	}

	public void onPlayerDisconnect(DisconnectEvent event) {
		// 获取玩家昵称
		String playerUsername = event.getPlayer().getUsername();
		// 将玩家退出群组的消息发送给所有人
		proxyServer.getAllServers().forEach(server -> {
			server.sendMessage(Component.text("§8[§c-§8]§r " + playerUsername + " §2离开了群组"));
		});
	}
}
