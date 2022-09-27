package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.util.FutureUtil;
import xyz.fcidd.velocity.chat.util.ComponentUtil;

import java.util.Optional;

import static xyz.fcidd.velocity.chat.util.PluginUtil.PLAYER_LIST;
import static xyz.fcidd.velocity.chat.util.PluginUtil.PROXY_SERVER;

public class PlayerLoginServerListener {
	@Subscribe
	public void onPlayerConnectedAsync(ServerConnectedEvent event) {
		FutureUtil.thenRun(() -> onPlayerConnected(event));
	}

	public void onPlayerConnected(ServerConnectedEvent event) {
		// 获取玩家信息
		Player player = event.getPlayer();
		// 获取目标服务器
		RegisteredServer targetServer = event.getServer();
		// 获取目标服务器消息组件
		Component targetServerComponent = ComponentUtil.getServerComponent(targetServer);
		// 玩家名
		Component playerNameComponent = ComponentUtil.getPlayerComponent(player);
		// 获取来源服务器Optional
		Optional<RegisteredServer> serverOptional = event.getPreviousServer();
		// 判断是否有来源服务器
		if (serverOptional.isEmpty()) {
			// 向玩家列表添加
			PLAYER_LIST.put(player, new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId()));
			// 获取子服前缀
			// 玩家连接到服务器的消息
			Component connectionMessage = Component
					.text("§8[§2+§8]§r ")
					.append(playerNameComponent)
					.append(Component.text(" §2加入了§r"))
					.append(targetServerComponent);
			// 向所有的服务器发送玩家连接到服务器的消息
			PROXY_SERVER.sendMessage(connectionMessage);
			// 不要向当前玩家发送消息，显示不出来的
		} else {
			RegisteredServer sourceServer = serverOptional.get();
			// 玩家切换服务器的消息
			Component connectionMessage = Component
					.text("§8[§b⇄§8]§r ")
					.append(playerNameComponent)
					.append(Component.text(" §2从§r"))
					// 来源服务器
					.append(ComponentUtil.getServerComponent(sourceServer))
					.append(Component.text("§2切换到了§r"))
					// 目标服务器
					.append(targetServerComponent);
			// 向所有的服务器发送玩家切换服务器的消息
			PROXY_SERVER.sendMessage(connectionMessage);
		}
	}
}
