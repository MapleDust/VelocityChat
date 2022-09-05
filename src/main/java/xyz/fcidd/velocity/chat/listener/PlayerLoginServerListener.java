package xyz.fcidd.velocity.chat.listener;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.FutureUtil;

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
		String playerUsername = player.getUsername();
		// 获取配置文件的服务器名称及前缀
		Toml configServerList = config.getSubPrefix();
		// 获取玩家连接的服务器的名称
		String serverName = event.getServer().getServerInfo().getName();
		// 获取上一个服务器
		RegisteredServer registeredServer = event.getPreviousServer().orElse(null);
		// 获取上一个服务器的名称
		String previousServerName = null;
		if (registeredServer != null) {
			previousServerName = registeredServer.getServerInfo().getName();
		}
		if (previousServerName == null) {
			// 获取子服前缀
			String subPrefix = configServerList.getString(serverName);
			if (subPrefix == null) subPrefix = serverName;
			// 玩家连接到服务器的消息
			String connectionMessage = "§8[§2+§8]§r " + playerUsername + " §2通过群组加入了§r " + subPrefix;
			// 向所有的服务器发送玩家连接到服务器的消息
			proxyServer.getAllServers().forEach(server -> server.sendMessage(Component.text(connectionMessage)));
			player.sendMessage(Component.text("§8[§2+§8]§r " + playerUsername + " §2通过群组加入了§r " + subPrefix));
		} else {
			// 获取连接的子服前缀
			String subPrefix = configServerList.getString(serverName);
			if (subPrefix == null) subPrefix = serverName;
			// 获取上一个连接的子服前缀
			String previousServerSubPrefix = configServerList.getString(previousServerName);
			if (subPrefix == null) previousServerSubPrefix = previousServerName;
			// 玩家切换服务器的消息
			Component connectionMessage = Component.text("§8[§b⇄§8]§r " + playerUsername + " §2从§r " + previousServerSubPrefix + " §2切换到§r " + subPrefix);
			// 向所有的服务器发送玩家切换服务器的消息
			proxyServer.getAllServers().forEach(server -> server.sendMessage(connectionMessage));
			player.sendMessage(connectionMessage);
		}
	}
}
