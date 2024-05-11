package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.command.Commands;
import xyz.fcidd.velocity.chat.message.Components;
import xyz.fcidd.velocity.chat.util.CharacterUtils;
import xyz.fcidd.velocity.chat.util.ComponentUtils;

import java.util.List;

import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class CommandExecuteListener {
	private static final Logger logger = VelocityChatPlugin.getLogger();

	@Subscribe
	public void onCommandExecute(@NotNull CommandExecuteEvent event) {
		if (!event.getResult().isAllowed()
			|| !(event.getCommandSource() instanceof Player sourcePlayer)) {
			return;
		}

		String command = event.getCommand();
		List<String> commandNodes = List.of(command.split(" "));

		String rootCommand = commandNodes.get(0);
		if (rootCommand.equals("execute")) {
			return;
		}

		int size = commandNodes.size();
		// TODO 换更好的实现。本服务器拒绝命令后再执行跨服tp、修改命令提示
		if (CharacterUtils.equalsAny(rootCommand, Commands.TELEPORT)) {
			if (!CONFIG.isCommandTeleportSwitchServers()) {
				return;
			}
			if (size == 2) { // /tp <target>
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, commandNodes.get(1))) {
					event.setResult(denied());
				}
			} else if (size == 3 // /tp <source> <target>
				&& commandNodes.get(size - 2).equals(sourcePlayer.getUsername())) {
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, commandNodes.get(2))) {
					event.setResult(denied());
				}
			} // else: /tp <x> <y> <z>

			logCommand(sourcePlayer, command);
			return;
		}

		// TODO 单独控制 log tells?
		if (CharacterUtils.equalsAny(rootCommand, Commands.TELLs) && size >= 3) { // /tell <target> <message>...
			PROXY_SERVER.getPlayer(commandNodes.get(1)).ifPresent(targetPlayer -> {
				// 如果不在同个服务器则接管该指令的执行
				if (PLAYER_UTIL.hasTheSameServer(sourcePlayer, targetPlayer)) {
					return;
				}
				event.setResult(denied());
				TextComponent tellMessage = Component.text(String.join(" ", commandNodes.subList(2, size - 1)));
				// 发送私聊
				targetPlayer.sendMessage(Components.TELL_MESSAGE.args(
					ComponentUtils.getPlayerComponent(sourcePlayer),
					tellMessage
				));
				// 发送反馈
				sourcePlayer.sendMessage(Components.TELL_SUCCEED.args(
					ComponentUtils.getPlayerComponent(targetPlayer),
					tellMessage
				));
			});

			if (CONFIG.isLogPlayerTells()) {
				logCommand(sourcePlayer, command);
			}
			return;
		}

		logCommand(sourcePlayer, command);
	}

	private static void logCommand(Player sourcePlayer, String command) {
		if (CONFIG.isLogPlayerCommands()) {
			sourcePlayer.getCurrentServer().ifPresentOrElse(
				server -> logger.info(
					"[command][{}]<{}> /{}",
					server.getServer().getServerInfo().getName(),
					sourcePlayer.getUsername(),
					command),
				() -> logger.info(
					"[command][]<{}> /{}",
					sourcePlayer.getUsername(),
					command));
		}
	}
}
