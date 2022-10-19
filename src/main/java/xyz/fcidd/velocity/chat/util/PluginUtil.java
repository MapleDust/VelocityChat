package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

import java.util.HashMap;
import java.util.Optional;

public class PluginUtil {
	public static final Logger LOGGER = VelocityChatPlugin.getLogger();
	public static final ProxyServer PROXY_SERVER = VelocityChatPlugin.getProxyServer();
	public static final HashMap<Player, ServerPing.SamplePlayer> PLAYER_LIST = new HashMap<>();
	public static final VelocityChatPlugin PLUGIN = VelocityChatPlugin.getInstance();

	private static final ServerPing.SamplePlayer[] PLAYER_ARRAY_TEMPLATE = new ServerPing.SamplePlayer[0];

	public static @NotNull Optional<Player> getPlayer(String name) {
		for (Player player : PROXY_SERVER.getAllPlayers()) {
			if (player.getUsername().equalsIgnoreCase(name)) return Optional.of(player);
		}
		return Optional.empty();
	}

	public static ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return PLAYER_LIST.values().toArray(PLAYER_ARRAY_TEMPLATE);
	}

	public static boolean isSameServer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
		Optional<RegisteredServer> sourceServerOptional = sourcePlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (sourceServerOptional.isEmpty()) return false;
		Optional<RegisteredServer> targetServerOptional = targetPlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		return targetServerOptional.isPresent()
			&& sourceServerOptional.get().equals(targetServerOptional.get());
	}
}
