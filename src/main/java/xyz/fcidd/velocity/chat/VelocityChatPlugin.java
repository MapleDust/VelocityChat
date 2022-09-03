package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.listener.CommandExecuteListener;
import xyz.fcidd.velocity.chat.listener.PlayerChatListener;
import xyz.fcidd.velocity.chat.listener.PlayerDisconnectListener;
import xyz.fcidd.velocity.chat.listener.PlayerLoginServerListener;

import java.nio.file.Path;

@Plugin(id = "velocity_chat", name = "VelocityChat", version = "1.3.0",
		authors = "MapleDust", url = "https://github.com/MapleDust/VelocityChat")
public class VelocityChatPlugin {
	@Getter
	private static ProxyServer proxyServer;
	@Getter
	private static Logger logger;
	public static final Path DATA_DIRECTORY = Path.of("./plugins/VelocityChat/");

	@Inject
	public VelocityChatPlugin(ProxyServer proxyServer, Logger logger) {
		VelocityChatPlugin.proxyServer = proxyServer;
		VelocityChatPlugin.logger = logger;
		// 初始化插件
		Initialization.init();
		logger.info("§aVelocityChat 已载入完成,项目地址 https://github.com/MapleDust/VelocityChat");
	}

	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		// 命令执行监听器
		proxyServer.getEventManager().register(this, new CommandExecuteListener());
		// 玩家聊天消息监听器
		proxyServer.getEventManager().register(this, new PlayerChatListener());
		// 玩家连接服务器监听器
		proxyServer.getEventManager().register(this, new PlayerLoginServerListener());
		// 玩家断开服务器监听器
		proxyServer.getEventManager().register(this, new PlayerDisconnectListener());
	}
}
