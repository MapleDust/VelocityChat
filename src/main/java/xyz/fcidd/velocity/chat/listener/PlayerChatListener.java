package xyz.fcidd.velocity.chat.listener;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VCCConfig;
import xyz.fcidd.velocity.chat.util.MinecraftColorCodeUtil;

import java.util.Optional;

import static xyz.fcidd.velocity.chat.util.ILogger.*;

public class PlayerChatListener {
	private final ProxyServer proxyServer = VelocityChatPlugin.getProxyServer();
	private final VCCConfig config = ConfigManager.getConfig();

	@Subscribe
	public EventTask onPlayerChatAsync(PlayerChatEvent event) {
		return EventTask.async(() -> onPlayerChat(event));
	}

	private void onPlayerChat(PlayerChatEvent event) {
		// 获取玩家发送的消息
		String playerMessage = MinecraftColorCodeUtil.replaceColorCode(event.getMessage());
		// 获取玩家消息的长度
		int playerMessageLength = playerMessage.length();
		// 初始化迭代后的 MCDR 命令前缀
		String finalMcdrCommandPrefix = null;
		// 将 MCDR 命令前缀列表进行迭代
		for (String mcdrCommandPrefix : config.getMcdrCommandPrefix()) {
			// 有可能会发生字符串下标越界异常，需要简单的处理一下
			if (playerMessageLength > mcdrCommandPrefix.length()
					&& playerMessage.startsWith(mcdrCommandPrefix)) {
				// 获取 MCDR 命令前缀
				finalMcdrCommandPrefix = mcdrCommandPrefix;
			}
		}
		// 如果迭代后的 MCDR 命令前缀为空
		if (finalMcdrCommandPrefix == null) {
			// 取消消息发送
			event.setResult(PlayerChatEvent.ChatResult.denied());
			// 获取所有配置文件的子服名称和子服前缀
			Toml configServerList = config.getSubPrefix();
			// 获取玩家信息
			Player player = event.getPlayer();
			// 获取服务器昵称
			String serverName;
			Optional<ServerConnection> currentServer = player.getCurrentServer();
			if (currentServer.isEmpty()) {
				serverName = "NULL";
			} else {
				serverName = currentServer.get().getServer().getServerInfo().getName();
			}
			// 获取子服的前缀
			String subPrefix = configServerList.getString(serverName);
			if (subPrefix == null) subPrefix = serverName;
			// 获取玩家昵称
			String playerUsername = player.getUsername();
			// 如果打印玩家消息日志
			CHAT_LOGGER.info("[{}]<{}> {}", serverName, playerUsername, playerMessage);
			// 处理后的玩家消息
			Component modifiedMessage = Component.text(config.getMainPrefix() + subPrefix + "§r<" + playerUsername + "> " + playerMessage);
			// 向所有服务器发送处理后的玩家消息
			proxyServer.getAllServers().forEach(registeredServer -> registeredServer.sendMessage(modifiedMessage));
		}
	}
}
