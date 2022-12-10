package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import fun.qu_an.lib.minecraft.vanilla.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.command.Commands;
import xyz.fcidd.velocity.chat.text.Components;
import xyz.fcidd.velocity.chat.text.Translates;

import java.util.List;

import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.LogUtils.LOGGER;
import static xyz.fcidd.velocity.chat.util.Utils.PLAYER_UTIL;

public class CommandExecuteListener {
	@Subscribe(order = PostOrder.FIRST, async = false) // 尽可能减少异步执行带来的输出顺序影响
	public void onCommandExecuteFirst(@NotNull CommandExecuteEvent event) {
		if (!event.getResult().isAllowed()
			|| !(event.getCommandSource() instanceof Player sourcePlayer)) {
			return;
		}
		// 打印
		if (CONFIG.isLogPlayerCommand()) {
			sourcePlayer.getCurrentServer().ifPresentOrElse(
				server -> LOGGER.info(
					"[cmd][{}]<{}> /{}",
					server.getServer().getServerInfo().getName(),
					sourcePlayer.getUsername(),
					event.getCommand()),
				() -> LOGGER.info(
					"[cmd][]<{}> /{}",
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

		int i = CommandUtils.indexOfRoot(command, Commands.TELEPORT);
		if (i == 0) { // 非execute
			if (i == size - 2) { // /tp <target>
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, command.get(i + 1))) {
					event.setResult(denied());
				}
			} else if (i == size - 3 // /tp <source> <target>
				&& command.get(size - 2).equals(sourcePlayer.getUsername())) {
				// 跨服tp
				if (PLAYER_UTIL.tpWithServerSwitch(sourcePlayer, command.get(i + 2))) {
					event.setResult(denied());
				}
			} // else: /tp <x> <y> <z>
			return;
		}

		int j = CommandUtils.indexOfRoot(command, Commands.TELL);
		if (j == 0 // 非execute
			&& j <= size - 3) { // /tell <target> <message>...
			PLAYER_UTIL.getPlayerByName(command.get(j + 1)).ifPresent(targetPlayer -> {
				// 如果不在同个服务器则接管该指令的执行
				if (!PLAYER_UTIL.hasTheSameServer(sourcePlayer, targetPlayer)) {
					event.setResult(denied());
					TextComponent tellMessage = Component.text(String.join(" ", command.subList(2, size - 1)));
					// 发送私聊
					targetPlayer.sendMessage(Translates.TELL_MESSAGE.args(
						Components.getPlayerComponent(sourcePlayer),
						tellMessage
					));
					// 发送反馈
					sourcePlayer.sendMessage(Translates.TELL_RESPONSE.args(
						Components.getPlayerComponent(targetPlayer),
						tellMessage
					));
				}
			});
		}
	}
}
