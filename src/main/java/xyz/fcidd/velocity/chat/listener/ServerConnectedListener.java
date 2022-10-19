package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import fun.qu_an.lib.velocity.util.TaskUtils;
import xyz.fcidd.velocity.chat.component.Translates;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.component.Components;
import xyz.fcidd.velocity.chat.util.TabListUtil;
import xyz.fcidd.velocity.chat.util.MessageTaskUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static fun.qu_an.lib.velocity.util.PluginUtils.PLAYER_LIST;
import static fun.qu_an.lib.velocity.util.PluginUtils.PROXY_SERVER;

public class ServerConnectedListener {
	@Subscribe(order = PostOrder.LAST, async = false)
	// 尽可能避免因异步执行导致事件处理时间超过1s，错过tab列表更新
	public void onPlayerConnectedLast(ServerConnectedEvent event) {
		if (VelocityChatConfig.CONFIG.isShowGlobalTabList()) {
			TaskUtils.delay(1, TimeUnit.SECONDS, TabListUtil::update);
		}
	}

	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerConnectedFirstSync(@NotNull ServerConnectedEvent event) {
		MessageTaskUtil.runInMessageThread(() -> {
			Player player = event.getPlayer();
			RegisteredServer targetServer = event.getServer();
			// 获取目标服务器消息组件
			Component targetServerComponent = Components.getServerComponent(targetServer);
			// 玩家名
			Component playerNameComponent = Components.getPlayerComponent(player);
			// 获取来源服务器Optional
			Optional<RegisteredServer> serverOptional = event.getPreviousServer();
			// 判断是否刚刚连接至服务器（是否没有来源服务器）
			if (serverOptional.isEmpty()) {
				// 向玩家列表添加
				PLAYER_LIST.put(player, new ServerPing.SamplePlayer(
					player.getUsername(),
					player.getUniqueId()));
				// 发送服务器连接消息
				PROXY_SERVER.sendMessage(Translates.CONNECTED.args(
					playerNameComponent,
					targetServerComponent
				));
			} else {
				RegisteredServer sourceServer = serverOptional.get();
				// 发送服务器切换消息
				PROXY_SERVER.sendMessage(Translates.SERVER_SWITCH.args(
					playerNameComponent,
					// 来源服务器
					Components.getServerComponent(sourceServer),
					// 目标服务器
					targetServerComponent)
				);
			}
		});
	}
}
