package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtil {
	private static final Map<String, Component> PLAYER_COMPONENT_CACHE = new HashMap<>();

	public static Component getPlayerComponent(Player player) {
		String playerName = player.getUsername();
		if (PLAYER_COMPONENT_CACHE.containsKey(playerName)) {
			return PLAYER_COMPONENT_CACHE.get(playerName);
		}
		Component playerComponent = Component.text(playerName)
				.hoverEvent(player.asHoverEvent())
				.clickEvent(ClickEvent.clickEvent(
						ClickEvent.Action.SUGGEST_COMMAND,
						"/tell " + playerName + " "
				));
		PLAYER_COMPONENT_CACHE.put(playerName, playerComponent);
		return playerComponent;
	}

	public static void removeFromCache(Player player) {
		removeFromCache(player.getUsername());
	}

	public static void removeFromCache(String playerName) {
		PLAYER_COMPONENT_CACHE.remove(playerName);
	}
}
