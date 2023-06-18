package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.text.Components;
import xyz.fcidd.velocity.chat.listener.*;

import java.nio.file.Path;

import static xyz.fcidd.velocity.chat.BuildConstants.*;
import static xyz.fcidd.velocity.chat.text.Translates.LANGUAGE_MANAGER;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;

@Plugin(id = PLUGIN_ID,
		name = PLUGIN_NAME,
		version = VERSION,
		authors = {"MapleDust", "Harvey_Husky"}
)
public class VelocityChatPlugin {
	@Getter
	private static VelocityChatPlugin instance;
	@Getter
	private static ProxyServer proxyServer;
	@Getter
	private static Logger logger;
	public static final Path DATA_DIRECTORY = Path.of("plugins").resolve(PLUGIN_NAME);

	@Inject
	public VelocityChatPlugin(ProxyServer proxyServer, Logger logger) {
		VelocityChatPlugin.proxyServer = proxyServer;
		VelocityChatPlugin.logger = logger;
		VelocityChatPlugin.instance = this;
	}

	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		// create
		load();
		// 注册事件
		EventManager eventManager = proxyServer.getEventManager();
		// 命令执行
		eventManager.register(this, new CommandExecuteListener());
		// 玩家聊天消息
		eventManager.register(this, new PlayerChatListener());
		// 玩家连接/切换服务器
		eventManager.register(this, new ServerConnectedListener());
		// 玩家断开服务器
		eventManager.register(this, new DisconnectListener());
		// 玩家ping
		eventManager.register(this, new ProxyPingListener());

		logger.info("§a" + PLUGIN_NAME + " v" + VERSION + " 已加载！");
	}

	@Subscribe
	public void onProxyReload(ProxyReloadEvent event) {
		reload();
	}

	public static void reload() {
		Components.resetCache();
		load();
	}

	private static void load() {
		CONFIG.load();
		LANGUAGE_MANAGER.loadOrReload();
		// glist 权限
		PLAYER_UTIL.registerPermission("velocity.command.glist", player -> CONFIG.isEnableCommandGlist());
	}
}
