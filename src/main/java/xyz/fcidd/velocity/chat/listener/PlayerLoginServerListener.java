package xyz.fcidd.velocity.chat.listener;

import com.electronwill.nightconfig.core.Config;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.config.VelocityChatConfigs;
import xyz.fcidd.velocity.chat.util.FutureUtil;
import xyz.fcidd.velocity.chat.util.PlayerUtil;

import java.util.List;

public class PlayerLoginServerListener {
	private final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();
	private final VelocityChatConfig config = ConfigManager.getConfig();

	@Subscribe
	public void onPlayerConnectedAsync(ServerConnectedEvent event) {
		FutureUtil.thenRun(() -> onPlayerConnected(event));
	}

	public void onPlayerConnected(ServerConnectedEvent event) {
		// 获取玩家信息
		Player player = event.getPlayer();
		// 获取玩家昵称
		String playerName = player.getUsername();
		// 获取配置文件的服务器名称及前缀
		Config serverNames = config.getServerNames();
		// 获取玩家连接的服务器的名称
		String targetServerId = event.getServer().getServerInfo().getName();
		// 获取上一个服务器
		RegisteredServer registeredServer = event.getPreviousServer().orElse(null);
		// 获取上一个服务器的名称
		String sourceServerId = null;
		if (registeredServer != null) {
			sourceServerId = registeredServer.getServerInfo().getName();
		}
		// 获取服务器名称
		String targetServerName;
		List<String> list = VelocityChatConfigs.getList(serverNames, targetServerId);
		if (list == null || list.isEmpty()) {
			targetServerName = targetServerId;
		} else if (list.size() == 1) {
			targetServerName = list.get(0);
		} else {
			targetServerName = list.get(1);
		}
		// 玩家名
		Component playerNameComponent = PlayerUtil.getPlayerComponent(player);
		// 判断是否有来源服务器
		if (sourceServerId == null) {
			// 获取子服前缀
			// 玩家连接到服务器的消息
			final Component connectionMessage = Component
					.text("§8[§2+§8]§r ")
					.append(playerNameComponent)
					.append(Component.text(" §2通过群组加入了§r "
							+ targetServerName));
			// 向所有的服务器发送玩家连接到服务器的消息
			proxyServer.getAllServers().forEach(server -> server.sendMessage(connectionMessage));
			// 不要向玩家发送消息，显示不出来的
		} else {
			// 获取连接的子服前缀
			// 获取上一个连接的子服前缀
			String sourceServerName;
			list = VelocityChatConfigs.getList(serverNames, sourceServerId);
			if (list == null || list.isEmpty()) {
				sourceServerName = sourceServerId;
			} else if (list.size() == 1) { // 如果只存在一个名称
				sourceServerName = list.get(0);
			} else { // 如果存在两个名称，则使用第二个，切换/进出服务器时显示的名称
				sourceServerName = list.get(1);
			}
			// 玩家切换服务器的消息
			final Component connectionMessage = Component
					.text("§8[§b⇄§8]§r ")
					.append(playerNameComponent)
					.append(Component.text(" §2从§r "
							+ sourceServerName
							+ " §2切换到§r "
							+ targetServerName));
			// 向所有的服务器发送玩家切换服务器的消息
			proxyServer.getAllServers().forEach(server -> server.sendMessage(connectionMessage));
			player.sendMessage(connectionMessage);
		}
	}
}
