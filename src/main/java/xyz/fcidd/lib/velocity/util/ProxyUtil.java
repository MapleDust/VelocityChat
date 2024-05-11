package xyz.fcidd.lib.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ProxyUtil {
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull ProxyUtil create(ProxyServer proxyServer) {
		return new ProxyUtil(proxyServer);
	}

	/**
	 * 群组服务器API
	 */
	public final ProxyServer proxyServer;

	@Contract(pure = true)
	ProxyUtil(ProxyServer proxyServer) {
		this.proxyServer = proxyServer;
	}

	/**
	 * 判断服务器是否处于在线模式
	 */
	public boolean isOnlineMode() {
		return proxyServer.getConfiguration().isOnlineMode();
	}

	public @NotNull List<String> getServerIds() {
		return proxyServer
			.getAllServers()
			.stream()
			.map(server -> server.getServerInfo().getName())
			.toList();
	}

	public @NotNull Component getPlayerConnectedComponent(@NotNull RegisteredServer server) {
		int onlinePlayers = server.getPlayersConnected().size();
		if (onlinePlayers == 1) {
			return Component
				.translatable("velocity.command.server-tooltip-player-online")
				.args(Component.text(onlinePlayers));
		} else {
			return Component
				.translatable("velocity.command.server-tooltip-players-online")
				.args(Component.text(onlinePlayers));
		}
	}

	public @NotNull Component getServerComponent(@NotNull Component rawComponent, @NotNull RegisteredServer server) {
		return rawComponent
			.clickEvent(ClickEvent.runCommand("/server " + server.getServerInfo().getName()))
			.hoverEvent(HoverEvent.showText(getPlayerConnectedComponent(server)));
	}

	public @NotNull Component getServerComponent(@NotNull Component rawComponent, @NotNull RegisteredServer server, boolean isCurrentServer) {
		if (isCurrentServer) {
			return rawComponent
				.hoverEvent(HoverEvent
					.showText(Component
						.translatable("velocity.command.server-tooltip-current-server")
						.append(Component.newline())
						.append(getPlayerConnectedComponent(server))));
		}
		return rawComponent
			.clickEvent(ClickEvent.runCommand("/server " + server.getServerInfo().getName()))
			.hoverEvent(HoverEvent
				.showText(Component
					.translatable("velocity.command.server-tooltip-offer-connect-server")
					.append(Component.newline())
					.append(getPlayerConnectedComponent(server))));
	}
}
