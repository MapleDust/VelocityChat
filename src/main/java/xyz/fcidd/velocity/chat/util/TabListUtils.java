package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class TabListUtils {
	public static void refresh() {
		Collection<Player> allPlayers = PROXY_SERVER.getAllPlayers();
		for (Player player1 : allPlayers) {
			TabList tabList = player1.getTabList();
			RegisteredServer server1 = player1
				.getCurrentServer()
				.map(ServerConnection::getServer)
				.orElse(null);
			for (Player player2 : allPlayers) {
				if (player1.equals(player2)) continue;
				Optional<ServerConnection> optional2 = player2.getCurrentServer();
				if (optional2.isPresent() && optional2.get().getServer().equals(server1)) {
					continue;
				}
				tabList.addEntry(tabList
					.removeEntry(player2.getUniqueId())
					.orElse(TabListEntry
						.builder()
						.tabList(tabList)
						.profile(player2.getGameProfile())
						.build())
					.setDisplayName(Component
						.text(player2.getUsername())
						.style(Style
							.style(TextColor.color(0x6D8BBF),
								TextDecoration.UNDERLINED,
								TextDecoration.ITALIC))));
			}
		}
	}

	public static void reload() {
		if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
			refresh();
		} else {
			reset();
		}
	}

	public static void reset() {
		Collection<Player> allPlayers = PROXY_SERVER.getAllPlayers();
		for (Player player1 : allPlayers) {
			TabList tabList = player1.getTabList();
			RegisteredServer server1 = player1
				.getCurrentServer()
				.map(ServerConnection::getServer)
				.orElse(null);
			for (Player player2 : allPlayers) {
				if (player1.equals(player2)) continue;
				Optional<ServerConnection> optional2 = player2.getCurrentServer();
				if (optional2.isPresent() && optional2.get().getServer().equals(server1)) {
					continue;
				}
				tabList.removeEntry(player2.getUniqueId());
			}
		}
	}

	public static void remove(@NotNull Player player) {
		UUID uniqueId = player.getUniqueId();
		PROXY_SERVER.getAllPlayers().forEach(player1 -> player1.getTabList().removeEntry(uniqueId));
	}
}
