package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.message.Components;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.TabListUtils;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class DisconnectListener {
	@Subscribe
	public void onPlayerDisconnect(@NotNull DisconnectEvent event) {
		Player player = event.getPlayer();
		// 玩家名
		Component playerNameComponent = ComponentUtils.getPlayerComponent(player);
		// 将玩家退出群组的消息发送给所有人
		PROXY_SERVER.sendMessage(Components.DISCONNECT.args(playerNameComponent));
		Caches.removePlayerCaches(player); // 移除玩家缓存
		if (CONFIG.isShowGlobalTabList()) {
			TabListUtils.remove(player); // 从 tab list 移除
		}
	}
}
