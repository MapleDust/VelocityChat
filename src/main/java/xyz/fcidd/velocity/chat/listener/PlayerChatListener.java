package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.qu_an.lib.basic.util.CharacterUtils;
import fun.qu_an.lib.minecraft.vanilla.util.FormatUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.util.Utils;

import java.util.List;
import java.util.Optional;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;

public class PlayerChatListener {
	private static final Logger logger = VelocityChatPlugin.getLogger();

	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onPlayerChatSyncFirst(@NotNull PlayerChatEvent event) {
		if (!CONFIG.isDefaultGlobalChat()) { // 是否接管聊天
			return;
		}
		// 获取玩家发送的消息
		String playerMessage = event.getMessage();
		if (CONFIG.isColorableChat()) {
			playerMessage = FormatUtils.replaceFormattingCode(playerMessage);
		}

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
				logger.info("[mcdr][{}]<{}> {}", serverId, playerName, playerMessage);
			}
			return;
		}

		// 取消消息发送！
		event.setResult(denied());

		// 发送全局消息！
		Utils.sendGlobalPlayerChat(player, playerMessage, currentServer, serverId);
	}
}
