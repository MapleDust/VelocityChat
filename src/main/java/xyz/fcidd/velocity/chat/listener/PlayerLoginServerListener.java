package xyz.fcidd.velocity.chat.listener;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VCCConfig;

public class PlayerLoginServerListener {
	private final ProxyServer proxyServer;

	public PlayerLoginServerListener(ProxyServer proxyServer) {
		this.proxyServer = proxyServer;
	}

	@Subscribe
	public void playerConnectionServer(ServerConnectedEvent connectedEvent) {

		// 获取玩家信息
		Player player = connectedEvent.getPlayer();
		// 获取玩家昵称
		String playerUsername = player.getUsername();
		// 获取玩家连接的服务器的名称
		String serverName = connectedEvent.getServer().getServerInfo().getName();
		//声明上一个服务器名称
		String previousServerName = null;
		// 获取上一个服务器
		RegisteredServer registeredServer = connectedEvent.getPreviousServer().orElse(null);
		// 获取上一个服务器的名称
		if (registeredServer != null) {
			previousServerName = registeredServer.getServerInfo().getName();
		}
		// 读取配置文件
		VCCConfig config = ConfigManager.getConfig();
		// 获取配置文件的服务器名称及前缀
		Toml configServerList = config.getSub_prefix();
		if (configServerList.contains(serverName)) {
			if (previousServerName == null) {
				//如果配置服务器的列表包含玩家连接的服务器并且上一个服务器名称为空

				// 获取子服前缀
				String subPrefix = configServerList.getString(serverName);
				// 向所有的服务器发送玩家连接到服务器的消息
				proxyServer.getAllServers().forEach(server -> {
					server.sendMessage(Component.text("§8[§2+§8]§r " + playerUsername + " §2通过群组加入了§r " + subPrefix));
				});
				player.sendMessage(Component.text("§8[§2+§8]§r " + playerUsername + " §2通过群组加入了§r " + subPrefix));
			} else if (configServerList.contains(previousServerName)) {
				// 如果配置文件的服务器包含玩家连接服务器的名称相同并且上个服务器名称不为空并且上个服务器名称在配置文件中

				// 获取连接的子服前缀
				Object subPrefix = configServerList.getString(serverName);
				// 获取上一个连接的子服前缀
				Object previousServerSubPrefix = configServerList.getString(previousServerName);
				// 向所有的服务器发送玩家切换服务器的消息
				proxyServer.getAllServers().forEach(server -> {
					server.sendMessage(Component.text("§8[§b⇄§8]§r " + playerUsername + " §2从§r " + previousServerSubPrefix + " §2切换到§r " + subPrefix));
				});
				player.sendMessage(Component.text("§8[§b⇄§8]§r " + playerUsername + " §2从§r " + previousServerSubPrefix + " §2切换到§r " + subPrefix));
			}
		}
	}
}
