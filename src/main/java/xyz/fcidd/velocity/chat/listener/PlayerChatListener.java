package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.util.TextUtil;
import xyz.fcidd.velocity.chat.util.ComponentUtil;
import xyz.fcidd.velocity.chat.util.ScheduleUtil;
import xyz.fcidd.velocity.chat.util.MinecraftColorCodeUtil;

import java.util.List;
import java.util.Optional;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.PluginUtil.*;

public class PlayerChatListener {
	@Subscribe
	public void onPlayerChat(@NotNull PlayerChatEvent event) {
		// 取消消息发送
		event.setResult(denied());
		ScheduleUtil.messageThread(() -> {
			// 获取玩家发送的消息
			String playerMessage = MinecraftColorCodeUtil.processColorCode(event.getMessage());

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
			List<String> mcdrCommandPrefixes = CONFIG.getMcdrCommandPrefix();
			if (!mcdrCommandPrefixes.isEmpty()
				&& TextUtil.startsWithAny(playerMessage, mcdrCommandPrefixes)) {
				if (CONFIG.isLogPlayerCommand()) {
					LOGGER.info("[mcdr][{}]<{}> {}", serverId, playerName, playerMessage);
				}
				return;
			}

			String[] chatFormat = CONFIG.getChatFormat(serverId);
			TextComponent.Builder builder = Component.text();
			// 玩家名
			Component playerNameComponent = ComponentUtil.getPlayerComponent(player);
			// 构建玩家消息，Velocity API 居然把玩家队伍颜色阻断掉了，导致不能显示玩家队伍颜色
			TextComponent chat_message = Component.text(playerMessage);
			for (String s : chatFormat) {
				switch (s) {
					case "{proxy_name}" -> builder.append(CONFIG.getProxyNameComponent());
					case "{server_name}" -> builder.append(ComponentUtil.getServerComponent(currentServer));
					case "{player_name}" -> builder.append(playerNameComponent);
					case "{chat_message}" -> builder.append(chat_message);
					default -> builder.append(Component.text(s));
				}
			}
			PROXY_SERVER.sendMessage(builder.build());
		});
	}
}
