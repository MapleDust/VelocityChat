package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.message.MessageChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Caches {
	private static final Map<Player, Component> playerComponentCache = new ConcurrentHashMap<>();
	private static final Map<Player, MessageChannel> playerChannelCache = new ConcurrentHashMap<>();
	private static final Map<RegisteredServer, Map.Entry<Integer, Component>> serverComponentCache = new ConcurrentHashMap<>(); // value: onlinePlayers, server component

	public static void setPlayerComponent(Player player, Component channel) {
		playerComponentCache.put(player, channel);
	}

	public static Component getPlayerComponent(Player player) {
		return playerComponentCache.get(player);
	}

	public static void setPlayerChannel(Player player, MessageChannel channel) {
		playerChannelCache.put(player, channel);
	}

	public static MessageChannel getPlayerChannel(Player player) {
		return playerChannelCache.get(player);
	}

	public static void removePlayerCaches(@NotNull Player player) {
		playerComponentCache.remove(player);
		playerChannelCache.remove(player);
	}

	public static void resetCaches() {
		playerComponentCache.clear();
		playerChannelCache.clear();
		serverComponentCache.clear();
	}

	public static Component getServerComponent(RegisteredServer server, int onlinePlayers) {
		Map.Entry<Integer, Component> entry = serverComponentCache.get(server);
		if (entry == null || entry.getKey() != onlinePlayers) {
			return null;
		}
		return entry.getValue();
	}

	public static void setServerComponent(RegisteredServer server, int onlinePlayers, Component serverComponent) {
		serverComponentCache.put(server, Map.entry(onlinePlayers, serverComponent));
	}
}
