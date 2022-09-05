package xyz.fcidd.velocity.chat.listener;

import com.electronwill.nightconfig.core.Config;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.BasicUtil;
import xyz.fcidd.velocity.chat.util.FutureUtil;
import xyz.fcidd.velocity.chat.util.MinecraftColorCodeUtil;

import java.util.Optional;

import static xyz.fcidd.velocity.chat.util.ILogger.*;

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

		// 如果是MCDR命令直接返回
		if (BasicUtil.startsWithAny(playerMessage, config.getMcdrCommandPrefix())) return;

		// 取消消息发送
		event.setResult(PlayerChatEvent.ChatResult.denied());
		// 获取所有配置文件的子服名称和子服前缀
		Config configServerList = config.getServerNames();
		// 获取玩家信息
		Player player = event.getPlayer();
		// 获取服务器昵称
		String serverId;
		String serverName;
		Optional<ServerConnection> currentServer = player.getCurrentServer();
		if (currentServer.isEmpty()) {
			serverId = "NULL";
			serverName = serverId;
		} else {
			serverId = currentServer.get().getServer().getServerInfo().getName();
			// 获取子服的前缀
			serverName = configServerList.get(serverId);
			if (serverName == null) serverName = "§8[§r" + serverId + "§8]";
		}
		// 获取玩家昵称
		String playerUsername = player.getUsername();
		// 如果打印玩家消息日志
		CHAT_LOGGER.info("[{}]<{}> {}", serverId, playerUsername, playerMessage);
		String[] chatFormat = config.getChatFormatArray();
		Component formattedChat = Component.empty();
		// 玩家名
		Component playerNameComponent = Component.text(playerUsername)
				.hoverEvent(player.asHoverEvent())
				.clickEvent(ClickEvent.clickEvent(
						ClickEvent.Action.SUGGEST_COMMAND,
						"/tell " + playerUsername + " "
				));
		// 构建玩家消息，Velocity API 居然把玩家队伍颜色阻断掉了，导致不能显示玩家队伍颜色
		for (int i = 0; i < chatFormat.length; i++) {
			String s = chatFormat[i];
			if (s.contains("$\\")) s = s.replace("$\\", "$");
			if (i == 0) {
				formattedChat = formattedChat.append(Component.text(s));
				continue;
			}
			formattedChat = switch (s) {
				case "{proxy_name}" -> formattedChat.append(Component.text(config.getProxyName()));
				case "{server_name}" -> formattedChat.append(Component.text(serverName));
				case "{player_name}" -> formattedChat.append(playerNameComponent);
				case "{chat_message}" -> formattedChat.append(Component.text(playerMessage));
				default -> formattedChat.append(Component.text(s));
			};
		}
		// 向所有服务器发送处理后的玩家消息
		final Component finalFormattedChat = formattedChat;
		proxyServer.getAllServers().forEach(registeredServer -> registeredServer.sendMessage(finalFormattedChat));
	}
}
