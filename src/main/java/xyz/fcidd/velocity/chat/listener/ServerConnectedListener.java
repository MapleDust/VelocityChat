package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.text.Translates;
import xyz.fcidd.velocity.chat.util.TabListUtils;

import java.util.concurrent.TimeUnit;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;
import static xyz.fcidd.velocity.chat.util.Utils.TASK_UTIL;

public class ServerConnectedListener {
	@Subscribe
	public void onPlayerConnected(ServerConnectedEvent event) {
		if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
			TASK_UTIL.delay(1, TimeUnit.SECONDS, TabListUtils::refresh);
		}
	}

	@Subscribe(order = PostOrder.FIRST, async = false)
	// 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerConnectedFirst(@NotNull ServerConnectedEvent event) {
		Player player = event.getPlayer();
		RegisteredServer targetServer = event.getServer();
		// 获取目标服务器消息组件
		Component targetServerComponent = ComponentUtils.getServerComponent(targetServer);
		// 玩家名
		Component playerNameComponent = ComponentUtils.getPlayerComponent(player);
		// 判断是否刚刚连接至服务器（是否没有来源服务器）
		event.getPreviousServer().ifPresentOrElse(
			server -> {
				// 发送服务器切换消息
				PROXY_SERVER.sendMessage(Translates.SERVER_SWITCH.args(
					playerNameComponent,
					ComponentUtils.getServerComponent(server),
					targetServerComponent)
				);
			}, () -> {
				// 发送服务器连接消息
				PROXY_SERVER.sendMessage(Translates.CONNECTED.args(
					playerNameComponent,
					targetServerComponent
				));
			});
	}
}
