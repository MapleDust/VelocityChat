package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.velocity.chat.translate.Translates;
import xyz.fcidd.velocity.chat.util.CommandUtil;
import xyz.fcidd.velocity.chat.util.ComponentUtil;
import xyz.fcidd.velocity.chat.util.PluginUtil;
import xyz.fcidd.velocity.chat.util.ScheduleUtil;

import java.util.List;
import java.util.Optional;

import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.denied;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.PluginUtil.LOGGER;
import static xyz.fcidd.velocity.chat.util.PluginUtil.PROXY_SERVER;

public class CommandExecuteListener {
	@Subscribe
	public void onCommandExecute(@NotNull CommandExecuteEvent event) {
		if (!event.getResult().isAllowed()
			|| !(event.getCommandSource() instanceof Player sourcePlayer)) {
			return;
		}

		// 打印
		if (CONFIG.isLogPlayerCommand()) {
			ScheduleUtil.messageThread(() ->
				sourcePlayer.getCurrentServer().ifPresentOrElse(
					server -> LOGGER.info(
						"[cmd][{}]<{}> /{}",
						server.getServer().getServerInfo().getName(),
						sourcePlayer.getUsername(),
						event.getCommand()),
					() -> LOGGER.info(
						"[cmd][]<{}> /{}",
						sourcePlayer.getUsername(),
						event.getCommand())));
		}

		// 接管一些指令

		List<String> command = List.of(event.getCommand().split(" "));
		int size = command.size();

		int i = CommandUtil.indexOfRoot(command, CommandUtil.TELEPORT);
		if (i == 0) { // 非execute
			if (i == size - 2) { // /tp <target>
				// 跨服tp
				event.setResult(denied());
				tpWithServerSwitch(event, sourcePlayer, command.get(i + 1));
			} else if (i == size - 3 // /tp <source> <target>
				&& command.get(size - 2).equals(sourcePlayer.getUsername())) {
				// 跨服tp
				event.setResult(denied());
				tpWithServerSwitch(event, sourcePlayer, command.get(i + 2));
			} // else: /tp <x> <y> <z>
			return;
		}

		int j = CommandUtil.indexOfRoot(command, CommandUtil.TELL);
		if (j == 0 // 非execute
			&& j <= size - 3) { // /tell <target> <message>...
			PluginUtil.getPlayer(command.get(j + 1)).ifPresent(targetPlayer -> {
				// 如果不在同个服务器则接管该指令的执行
				if (!PluginUtil.isSameServer(sourcePlayer, targetPlayer)) {
					event.setResult(denied());
					TextComponent tellMessage = Component.text(String.join(" ", command.subList(2, size - 1)));
					// 发送密语
					targetPlayer.sendMessage(Component.translatable(
						Translates.TELL_MESSAGE,
						ComponentUtil.getPlayerComponent(sourcePlayer),
						tellMessage
					));
					// 发送反馈
					sourcePlayer.sendMessage(Component.translatable(
						Translates.TELL_RESPONSE,
						ComponentUtil.getPlayerComponent(targetPlayer),
						tellMessage
					));
				}
			});
		}
	}

	private void tpWithServerSwitch(@NotNull CommandExecuteEvent event, @NotNull Player sourcePlayer, @NotNull String targetPlayerName) {
		PluginUtil.getPlayer(targetPlayerName).ifPresent(targetPlayer -> {
			if (switchToTargetPlayer(sourcePlayer, targetPlayer)) {
				event.setResult(denied());
				// 运行指令以tp
				PROXY_SERVER.getCommandManager().executeImmediatelyAsync(sourcePlayer, event.getCommand());
			}
		});
	}

	private boolean switchToTargetPlayer(@NotNull Player sourcePlayer, @NotNull Player targetPlayer) {
		Optional<RegisteredServer> targetServerOptional = targetPlayer
			.getCurrentServer()
			.map(ServerConnection::getServer);
		if (targetServerOptional.isPresent()) {
			RegisteredServer targetServer = targetServerOptional.get();
			Optional<RegisteredServer> sourceServerOptional = sourcePlayer
				.getCurrentServer()
				.map(ServerConnection::getServer);
			if (sourceServerOptional.isEmpty()
				|| !sourceServerOptional.get().equals(targetServer)) {
				// 来源为空或与目标不相等，则需要传送玩家
				return sourcePlayer
					.createConnectionRequest(targetServer)
					.connect()
					.join()
					.isSuccessful();
			}
		}
		return false;
	}
}
