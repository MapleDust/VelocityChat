package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.BasicUtil;
import xyz.fcidd.velocity.chat.util.ComponentUtil;
import xyz.fcidd.velocity.chat.util.FutureUtil;
import xyz.fcidd.velocity.chat.util.MinecraftColorCodeUtil;

import java.util.List;
import java.util.Optional;

import static xyz.fcidd.velocity.chat.util.ILogger.LOGGER;

public class PlayerChatListener {
	private final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();
	private final VelocityChatConfig config = ConfigManager.getConfig();

	@Subscribe
	public void onPlayerChat(PlayerChatEvent event) {
		FutureUtil.thenRun(() -> onPlayerChatImpl(event));
	}

	private void onPlayerChatImpl(PlayerChatEvent event) {
		// 获取玩家发送的消息
		String playerMessage = MinecraftColorCodeUtil.replaceColorCode(event.getMessage());

		// 获取玩家信息
		Player player = event.getPlayer();
		// 获取玩家名称
		String playerName = player.getUsername();
		// 获取服务器ID
		Optional<ServerConnection> currentServerOptional = player.getCurrentServer();
		RegisteredServer currentServer = null;
		String serverId = null;
		if (currentServerOptional.isPresent()) {
			currentServer = currentServerOptional.get().getServer();
			serverId = currentServer.getServerInfo().getName();
		}

		// 如果是MCDR命令直接返回
		List<String> mcdrCommandPrefixes = config.getMcdrCommandPrefix();
		if (!mcdrCommandPrefixes.isEmpty()
				&& BasicUtil.startsWithAny(playerMessage, mcdrCommandPrefixes)) {
			if (config.isLogPlayerCommand()) {
				LOGGER.info("[mcdr][{}]<{}> {}", serverId, playerName, playerMessage);
			}
			return;
		}

		// 取消消息发送
		event.setResult(PlayerChatEvent.ChatResult.denied());
		// 打印玩家消息日志
		LOGGER.info("[chat][{}]<{}> {}", serverId, playerName, playerMessage);
		String[] chatFormat = config.getChatFormatArray();
		Component formattedChat = Component.empty();
		// 玩家名
		Component playerNameComponent = ComponentUtil.getPlayerComponent(player);
		// 构建玩家消息，Velocity API 居然把玩家队伍颜色阻断掉了，导致不能显示玩家队伍颜色
		for (String s : chatFormat) {
			formattedChat = switch (s) {
				case "{proxy_name}" -> formattedChat.append(Component.text(config.getProxyName()));
				case "{server_name}" -> formattedChat.append(ComponentUtil.getServerChatComponent(currentServer));
				case "{player_name}" -> formattedChat.append(playerNameComponent);
				case "{chat_message}" -> formattedChat.append(Component.text(playerMessage));
				default -> formattedChat.append(Component.text(s));
			};
		}
		proxyServer.sendMessage(formattedChat);
	}
}
