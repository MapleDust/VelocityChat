package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.command.VchatCommand;
import xyz.fcidd.velocity.chat.listener.*;
import xyz.fcidd.velocity.chat.util.ComponentUtils;

import java.nio.file.Path;

import static xyz.fcidd.velocity.chat.BuildConstants.*;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.text.Translates.LANGUAGE_MANAGER;
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
		load(); // 1、2
		// 注册命令
		VchatCommand.register(); // 3
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

		logger.info("§a" + PLUGIN_NAME + " v" + VERSION + " loaded！");
	}

	@Subscribe
	public void onProxyReload(ProxyReloadEvent event) {
		load(); // 1、2
		ComponentUtils.resetCache();
		VchatCommand.reloadAlias(); // 3
		EventManager eventManager = proxyServer.getEventManager();
		// reload permissions
		proxyServer.getAllPlayers().forEach(player -> eventManager.fire(
			new PermissionsSetupEvent(player, subject -> permission1 -> Tristate.UNDEFINED)));
	}

	private static void load() {
		CONFIG.load(); // 1
		LANGUAGE_MANAGER.loadOrReload(); // 2
		// glist 权限
		PLAYER_UTIL.registerPermission("velocity.command.glist", player -> CONFIG.isEnableCommandGlist());
	}
}
