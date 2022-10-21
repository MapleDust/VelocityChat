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

import java.util.Optional;
import java.util.UUID;

import static fun.qu_an.lib.minecraft.velocity.util.ProxyUtils.PROXY_SERVER;

public class TabListUtils {
	public static void update() {
		PROXY_SERVER.getAllPlayers().forEach(player1 -> {
			TabList tabList = player1.getTabList();
			RegisteredServer server1 = player1
				.getCurrentServer()
				.map(ServerConnection::getServer)
				.orElse(null);
			PROXY_SERVER.getAllPlayers().forEach(player2 -> {
				if (player1.equals(player2)) return;
				Optional<ServerConnection> optional2 = player2.getCurrentServer();
				if (optional2.isPresent() && optional2.get().getServer().equals(server1)) {
					return;
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
			});
		});
	}

	public static void remove(@NotNull Player player) {
		UUID uniqueId = player.getUniqueId();
		PROXY_SERVER.getAllPlayers().forEach(player1 -> player1.getTabList().removeEntry(uniqueId));
	}
}
