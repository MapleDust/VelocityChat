package fun.qu_an.minecraft.velocity.api.util;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ApiPlayerUtil {
	private final HashMap<Player, ServerPing.SamplePlayer> playerList = new HashMap<>();
	private final ProxyServer proxyServer;

	ApiPlayerUtil(@NotNull Qu_anVelocityApi qu_anVelocityApi) {
		this.proxyServer = qu_anVelocityApi.getProxyServer();
		proxyServer.getEventManager().register(qu_anVelocityApi.getPlugin(),
			new Object() {
				@Subscribe
				public void onPlayerLogin(@NotNull LoginEvent event) {
					Player player = event.getPlayer();
					ApiPlayerUtil.this.playerList.put(player,
						new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId()));
				}

				@Subscribe
				public void onPlayerDisconnect(@NotNull DisconnectEvent event) {
					ApiPlayerUtil.this.playerList.remove(event.getPlayer());
				}
			});
	}

	public @NotNull Optional<Player> getPlayerByName(String name) {
		for (Player player : proxyServer.getAllPlayers()) {
			if (player.getUsername().equalsIgnoreCase(name)) return Optional.of(player);
		}
		return Optional.empty();
	}

	/*
	 * 仅用于 getSamplePlayers() 方法中的 List.toArray(T[]) 方法
	 */
	private static final ServerPing.SamplePlayer[] PLAYER_ARRAY_TEMPLATE = new ServerPing.SamplePlayer[0];

	public ServerPing.SamplePlayer @NotNull [] getSamplePlayers() {
		return playerList.values().toArray(PLAYER_ARRAY_TEMPLATE);
	}

	public boolean hasSameServer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
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
