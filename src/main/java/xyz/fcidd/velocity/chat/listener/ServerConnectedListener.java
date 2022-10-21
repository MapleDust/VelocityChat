package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.qu_an.lib.minecraft.velocity.util.TaskUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.component.Components;
import xyz.fcidd.velocity.chat.component.Translates;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.MessageTaskUtils;
import xyz.fcidd.velocity.chat.util.TabListUtils;

import java.util.concurrent.TimeUnit;

import static fun.qu_an.lib.minecraft.velocity.util.ProxyUtils.PROXY_SERVER;

public class ServerConnectedListener {
	@Subscribe(order = PostOrder.LAST, async = false)
	// 尽可能避免因异步执行导致事件处理时间超过1s，错过tab列表更新
	public void onPlayerConnectedLast(ServerConnectedEvent event) {
		if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
			TaskUtils.delay(1, TimeUnit.SECONDS, TabListUtils::update);
		}
	}

	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerConnectedFirstSync(@NotNull ServerConnectedEvent event) {
		MessageTaskUtils.runInMessageThread(() -> {
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
					PROXY_SERVER.sendMessage(Translates.SERVER_SWITCH.args(
						playerNameComponent,
						Components.getServerComponent(server),
						targetServerComponent)
					);
				}, () -> {
					// 发送服务器连接消息
					PROXY_SERVER.sendMessage(Translates.CONNECTED.args(
						playerNameComponent,
						targetServerComponent
					));
				}
			);
		});
	}
}
