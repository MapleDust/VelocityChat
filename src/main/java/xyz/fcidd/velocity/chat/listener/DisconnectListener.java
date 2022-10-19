package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.component.Translates;
import xyz.fcidd.velocity.chat.component.Components;
import xyz.fcidd.velocity.chat.util.TabListUtil;
import xyz.fcidd.velocity.chat.util.MessageTaskUtil;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static fun.qu_an.lib.velocity.util.PluginUtils.PLAYER_LIST;
import static fun.qu_an.lib.velocity.util.PluginUtils.PROXY_SERVER;

public class DisconnectListener {
	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerDisconnectSyncFirst(@NotNull DisconnectEvent event) {
		MessageTaskUtil.runInMessageThread(() -> {
			// 玩家名
			Component playerNameComponent = Components.getPlayerComponent(event.getPlayer());
			// 将玩家退出群组的消息发送给所有人
			PROXY_SERVER.sendMessage(Translates.DISCONNECT.args(playerNameComponent));
		});
	}

	@Subscribe
	public void onPlayerDisconnect(@NotNull DisconnectEvent event) {
		Player player = event.getPlayer();
		PLAYER_LIST.remove(player); // 从 ping 玩家列表移除
		Components.removeFromCache(player); // 移除玩家消息组件缓存
		if (CONFIG.isShowGlobalTabList()) TabListUtil.remove(player); // 从 tab list 移除
	}
}
