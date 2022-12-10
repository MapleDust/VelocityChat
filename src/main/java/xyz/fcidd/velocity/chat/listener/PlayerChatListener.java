package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.qu_an.lib.basic.util.CharacterUtils;
import fun.qu_an.lib.minecraft.vanilla.util.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.text.Components;
import xyz.fcidd.velocity.chat.text.Translates;

import java.util.List;
import java.util.Optional;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.LogUtils.LOGGER;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class PlayerChatListener {
	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerChatSyncFirst(@NotNull PlayerChatEvent event) {
		// 必须先取消消息发送再交给消息线程！
		event.setResult(denied());
		// 获取玩家发送的消息
		String playerMessage = FormatUtils.replaceFormattingCode(event.getMessage());

		// 获取玩家信息
		Player player = event.getPlayer();
		// 获取玩家名称
		String playerName = player.getUsername();
		// 获取服务器ID
		Optional<ServerConnection> currentServerOptional = player.getCurrentServer();
		RegisteredServer currentServer = null;
		String serverId = "";
		if (currentServerOptional.isPresent()) {
			currentServer = currentServerOptional.get().getServer();
			serverId = currentServer.getServerInfo().getName();
		}

		// 如果是MCDR命令直接返回
		List<String> mcdrCommandPrefixes = CONFIG.getMcdrCommandPrefix();
		if (!mcdrCommandPrefixes.isEmpty()
			&& CharacterUtils.startsWithAny(playerMessage, mcdrCommandPrefixes)) {
			if (CONFIG.isLogPlayerCommand()) {
				LOGGER.info("[mcdr][{}]<{}> {}", serverId, playerName, playerMessage);
			}
			return;
		}

		// 玩家名
		Component playerNameComponent = Components.getPlayerComponent(player);
		// 构建玩家消息，Velocity API 居然把玩家队伍颜色阻断掉了，导致不能显示玩家队伍颜色
		TextComponent chatMessage = Component.text(playerMessage);
		// 发送消息
		String serverChatKey = Translates.SERVER_CHAT + serverId;
		if (Translates.LANGUAGE_MANAGER.contains(serverChatKey)) {
			PROXY_SERVER.sendMessage(Component.translatable(
				serverChatKey, // 追加子服务器id
				Translates.PROXY_NAME, // 群组名称
				Components.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			));
		} else {
			PROXY_SERVER.sendMessage(Translates.DEFAULT_CHAT.args(
				Translates.PROXY_NAME, // 群组名称
				Components.getServerComponent(currentServer), // 服务器名称
				playerNameComponent, // 玩家名
				chatMessage // 聊天内容
			));
		}
	}
}
