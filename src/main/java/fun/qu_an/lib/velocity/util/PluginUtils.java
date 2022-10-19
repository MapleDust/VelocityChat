package fun.qu_an.lib.velocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import fun.qu_an.lib.velocity.Qu_anLibPlugin;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PluginUtils {
	public static final ProxyServer PROXY_SERVER = Qu_anLibPlugin.getProxyServer();
	public static final HashMap<Player, ServerPing.SamplePlayer> PLAYER_LIST = new HashMap<>();
	public static final GlobalTranslator GLOBAL_TRANSLATOR = GlobalTranslator.get();

	public static @NotNull Optional<Player> getPlayerByName(String name) {
		for (Player player : PROXY_SERVER.getAllPlayers()) {
			if (player.getUsername().equalsIgnoreCase(name)) return Optional.of(player);
		}
		return Optional.empty();
	}

	/*
	 * 仅用于 getSamplePlayers() 方法中的 List.toArray(T[]) 方法
	 */
	private static final ServerPing.SamplePlayer[] PLAYER_ARRAY_TEMPLATE = new ServerPing.SamplePlayer[0];

	public static ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return PLAYER_LIST.values().toArray(PLAYER_ARRAY_TEMPLATE);
	}

	public static boolean hasSameServer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
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
