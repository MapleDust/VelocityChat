package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import fun.qu_an.lib.basic.util.CharacterUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.command.Commands;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.text.Translates;

import java.util.List;

import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;

public class CommandExecuteListener {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onCommandExecuteFirst(@NotNull CommandExecuteEvent event) {
		if (!event.getResult().isAllowed()
			|| !(event.getCommandSource() instanceof Player sourcePlayer)) {
			return;
		}
		// 打印
		if (CONFIG.isLogPlayerCommand()) {
			sourcePlayer.getCurrentServer().ifPresentOrElse(
				server -> logger.info(
					"[command][{}]<{}> /{}",
					server.getServer().getServerInfo().getName(),
					sourcePlayer.getUsername(),
					event.getCommand()),
				() -> logger.info(
					"[command][]<{}> /{}",
					sourcePlayer.getUsername(),
					event.getCommand()));
		}
	}

	@Subscribe
	public void onCommandExecute(@NotNull CommandExecuteEvent event) {
		if (!event.getResult().isAllowed()
			|| !(event.getCommandSource() instanceof Player sourcePlayer)) {
			return;
		}

		// 接管一些指令

		List<String> command = List.of(event.getCommand().split(" "));
		int size = command.size();

		String rootCommand = command.get(0);
		if (rootCommand.equals("execute")) return;

		if (CharacterUtils.equalsAny(rootCommand, Commands.TELEPORT)) {
			if (size == 2) { // /tp <target>
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, command.get(1))) {
					event.setResult(denied());
				}
			} else if (size == 3 // /tp <source> <target>
				&& command.get(size - 2).equals(sourcePlayer.getUsername())) {
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, command.get(2))) {
					event.setResult(denied());
				}
			} // else: /tp <x> <y> <z>
			return;
		}

		if (CharacterUtils.equalsAny(rootCommand, Commands.TELL) && size >= 3) { // /tell <target> <message>...
			PLAYER_UTIL.getPlayerByName(command.get(1)).ifPresent(targetPlayer -> {
				// 如果不在同个服务器则接管该指令的执行
				if (!PLAYER_UTIL.hasTheSameServer(sourcePlayer, targetPlayer)) {
					event.setResult(denied());
					TextComponent tellMessage = Component.text(String.join(" ", command.subList(2, size - 1)));
					// 发送私聊
					targetPlayer.sendMessage(Translates.TELL_MESSAGE.args(
						ComponentUtils.getPlayerComponent(sourcePlayer),
						tellMessage
					));
					// 发送反馈
					sourcePlayer.sendMessage(Translates.TELL_RESPONSE.args(
						ComponentUtils.getPlayerComponent(targetPlayer),
						tellMessage
					));
				}
			});
		}
	}
}
