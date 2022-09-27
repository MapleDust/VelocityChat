package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;

import java.util.HashMap;

public class PluginUtil {
	public static final Logger LOGGER = VelocityChatPlugin.getLogger();
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final PlayerChatEvent.ChatResult DENIED = PlayerChatEvent.ChatResult.denied();
	public static final HashMap<Player, ServerPing.SamplePlayer> PLAYER_LIST = new HashMap<>();

	public static ServerPing.SamplePlayer[] getSamplePlayers() {
		return PLAYER_LIST.values().toArray(new ServerPing.SamplePlayer[0]);
	}
}
