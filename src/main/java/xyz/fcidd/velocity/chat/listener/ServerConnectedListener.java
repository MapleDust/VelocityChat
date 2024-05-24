package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.message.Components;
import xyz.fcidd.velocity.chat.util.TabListUtils;

import java.util.concurrent.TimeUnit;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;
import static xyz.fcidd.velocity.chat.util.Utils.TASK_UTIL;

public class ServerConnectedListener {
	@Subscribe()
	public void onPlayerConnected(@NotNull ServerConnectedEvent event) {
		Player player = event.getPlayer();
		RegisteredServer targetServer = event.getServer();
		// 获取目标服务器消息组件
		Component targetServerComponent = Components.getServerComponent(targetServer);
		// 玩家名
		Component playerNameComponent = Components.getPlayerComponent(player);
		// 判断是否刚刚连接至服务器（是否没有来源服务器）
		event.getPreviousServer().ifPresentOrElse(
			server -> {
				// 发送服务器切换消息
				PROXY_SERVER.sendMessage(Components.SERVER_SWITCH.args(
					playerNameComponent,
					Components.getServerComponent(server),
					targetServerComponent)
				);
			}, () -> {
				// 发送服务器连接消息
				PROXY_SERVER.sendMessage(Components.CONNECTED.args(
					playerNameComponent,
					targetServerComponent
				));
			});
		if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
			TASK_UTIL.delay(1, TimeUnit.SECONDS, TabListUtils::refresh);
		}
	}
}
