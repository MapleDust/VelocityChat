package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.ComponentUtil;
import xyz.fcidd.velocity.chat.util.ScheduleUtil;
import xyz.fcidd.velocity.chat.util.TabListUtil;

import static xyz.fcidd.velocity.chat.util.PluginUtil.PLAYER_LIST;
import static xyz.fcidd.velocity.chat.util.PluginUtil.PROXY_SERVER;

public class DisconnectListener {
	@Subscribe
	public void onPlayerDisconnect(@NotNull DisconnectEvent event) {
		ScheduleUtil.messageThread(() -> {
			Player player = event.getPlayer();
			PLAYER_LIST.remove(player); // 从玩家列表移除
			// 玩家名
			Component playerNameComponent = ComponentUtil.getPlayerComponent(player);
			// 将玩家退出群组的消息发送给所有人
			PROXY_SERVER.sendMessage(
				Component.text("§8[§c-§8]§r ")
					.append(playerNameComponent)
					.append(Component.text(" §2离开了群组"))
			);
			ComponentUtil.removeFromCache(player);
			if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
				TabListUtil.remove(player);
			}
		});
	}
}
