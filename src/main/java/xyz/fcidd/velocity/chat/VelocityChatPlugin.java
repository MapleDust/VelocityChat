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
import xyz.fcidd.lib.velocity.language.LanguageManager;
import xyz.fcidd.velocity.chat.command.VchatCommand;
import xyz.fcidd.velocity.chat.listener.*;
import xyz.fcidd.velocity.chat.message.Translates;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.TabListUtils;

import java.nio.file.Path;
import java.util.Set;

import static xyz.fcidd.velocity.chat.BuildConstants.*;
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
		load(); // 1、2
		// 注册命令
		VchatCommand.register(); // 3
		// 注册事件
		EventManager eventManager = proxyServer.getEventManager();
		eventManager.register(this, new CommandExecuteListener());
		eventManager.register(this, new PlayerChatListener());
		eventManager.register(this, new ServerConnectedListener());
		eventManager.register(this, new DisconnectListener());
		eventManager.register(this, new ProxyPingListener());

		logger.info("§a" + PLUGIN_NAME + " v" + VERSION + " loaded!");
	}

	@Subscribe
	public void onProxyReload(ProxyReloadEvent event) {
		reload();
	}

	public static void reload() {
		load(); // step 1 & 2
		VchatCommand.reloadAlias(); // step 3
		Caches.resetCaches();
		TabListUtils.reload();
		EventManager eventManager = proxyServer.getEventManager();
		// reload permissions
		proxyServer.getAllPlayers().forEach(player -> eventManager.fire(
			new PermissionsSetupEvent(player, subject -> permission1 -> Tristate.UNDEFINED)));
	}

	private static Set<?> languageManager_keys;

	private static void load() {
		CONFIG.load(); // step 1
		// step 2
		LanguageManager defaultLang = Translates.DEFAULT_LM;
		LanguageManager customLang = Translates.CUSTOM_LM;

		defaultLang.loadAndRegister();
		customLang.load();
		customLang.keys().forEach(defaultLang::unregister);
		customLang.register();
		// end step 2
		// glist 权限
		PLAYER_UTIL.registerPermission("velocity.command.glist", player -> CONFIG.isEnableCommandGlist());
	}
}
