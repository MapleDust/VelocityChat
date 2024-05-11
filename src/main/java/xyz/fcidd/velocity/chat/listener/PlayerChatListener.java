package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.MessageChannel;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.CharacterUtils;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.util.Utils;

import java.util.List;
import java.util.Optional;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class PlayerChatListener {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	static final int NON_CANCELLABLE_VERSION = ProtocolVersion.MINECRAFT_1_19_1.getProtocol();

	@Subscribe()
	public void onPlayerChat(@NotNull PlayerChatEvent event) {
		// 获取玩家发送的消息
		String message = event.getMessage();

		Player player = event.getPlayer();
		// 如果是MCDR命令直接返回
		List<String> mcdrCommandPrefixes = CONFIG.getMcdrCommandPrefix();
		if (!mcdrCommandPrefixes.isEmpty()
			&& CharacterUtils.startsWithAny(message, mcdrCommandPrefixes)) {
			if (CONFIG.isLogPlayerCommands()) {
				log("mcdr", message, player);
			}
			return;
		}

		// 检查子服务器聊天前缀
		String localChatPrefix = CONFIG.getLocalChatPrefix();
		if (message.startsWith(localChatPrefix)) {
			// 检查能否取消该玩家消息发送
//			if (!playerChatCancellable.getOrDefault(player, false)) {
//				return;
//			}
			// 能的话把前缀删掉
			event.setResult(denied());
			message = message.substring(localChatPrefix.length());
			player.spoofChatInput(message);
			if (CONFIG.isLogLocalChats()) { // 打印子服务器聊天内容
				log("local", message, player);
			}
			return;
		}
		String globalChatPrefix = CONFIG.getGlobalChatPrefix();
		// 检查全局聊天前缀
		if (message.startsWith(globalChatPrefix)) {
			message = message.substring(globalChatPrefix.length());
		} else {
			MessageChannel channel = Caches.getPlayerChannel(player);
			// 玩家没有指定频道，如果当前频道为子服务器则 return
			if (channel == null && !CONFIG.isDefaultGlobalChat()
				|| channel == MessageChannel.LOCAL) {
				if (CONFIG.isLogLocalChats()) { // 打印子服务器聊天内容
					log("local", message, player);
				}
				return;
			}
		}

		// 发送全局消息

		// 获取服务器ID
		Optional<ServerConnection> currentServerOptional = player.getCurrentServer();
		String serverId = "";
		RegisteredServer currentServer;
		if (currentServerOptional.isPresent()) {
			currentServer = currentServerOptional.get().getServer();
			serverId = currentServer.getServerInfo().getName();
		} else {
			currentServer = null;
		}

		// 格式化消息组件
		Component messageComponent;
		if (CONFIG.isFormattableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}

		// 检查能否取消该玩家消息发送
		if (CONFIG.isOverwriteLocalChats()
//			&& playerChatCancellable.getOrDefault(player, false)
		) {
			// 取消消息发送！
			event.setResult(denied());
			// 发送全局消息！
			Utils.sendGlobalPlayerChat(player, messageComponent, currentServer, serverId);
		} else {
			// 否则不接管所在服务器的聊天
			Component chatComponent = Utils.getGlobalPlayerChatComponent(player, messageComponent, currentServer, serverId);
			PROXY_SERVER.getConsoleCommandSource().sendMessage(chatComponent);
			for (RegisteredServer server : PROXY_SERVER.getAllServers()) {
				if (!server.equals(currentServer)) {
					server.sendMessage(chatComponent);
				}
			}
		}
	}

	private static void log(String head, String playerMessage, Player player) {
		String playerName = player.getUsername();
		Optional<ServerConnection> currentServerOptional = player.getCurrentServer();
		if (currentServerOptional.isPresent()) {
			String serverId = currentServerOptional.get().getServer().getServerInfo().getName();
			logger.info("[{}][{}]<{}> {}", head, serverId, playerName, playerMessage);
		} else {
			logger.info("[{}][]<{}> {}", head, playerName, playerMessage);
		}
	}
}
