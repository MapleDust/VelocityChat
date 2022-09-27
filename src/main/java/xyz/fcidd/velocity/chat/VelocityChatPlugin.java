package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.velocity.serverstatus.BuildConstants;
import lombok.Getter;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.listener.*;

import java.nio.file.Path;

@Plugin(id = BuildConstants.PLUGIN_ID,
		name = BuildConstants.PLUGIN_NAME,
		version = BuildConstants.VERSION,
		authors = {"MapleDust", "Harvey_Husky"},
		url = "https://github.com/MapleDust/VelocityChat")
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
	}

	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		ConfigManager.load();
		// 注册事件
		EventManager eventManager = proxyServer.getEventManager();
		// 命令执行
		eventManager.register(this, new CommandExecuteListener());
		// 玩家聊天消息
		eventManager.register(this, new PlayerChatListener());
		// 玩家连接服务器
		eventManager.register(this, new PlayerLoginServerListener());
		// 玩家断开服务器
		eventManager.register(this, new PlayerDisconnectListener());
		// 群组重载
		eventManager.register(this, new ProxyReloadListener());
		eventManager.register(this, new ProxyPingListener());

		logger.info("§aVelocityChat v" + BuildConstants.VERSION + " 已加载！项目地址：https://github.com/MapleDust/VelocityChat");
	}
}
