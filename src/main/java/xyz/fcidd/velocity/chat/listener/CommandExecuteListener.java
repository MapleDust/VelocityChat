package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.fcidd.velocity.chat.config.ConfigManager;
import xyz.fcidd.velocity.chat.config.VelocityChatConfig;
import xyz.fcidd.velocity.chat.util.FutureUtil;

import static xyz.fcidd.velocity.chat.util.ILogger.COMMAND_LOGGER;

public class CommandExecuteListener {
	private final VelocityChatConfig config = ConfigManager.getConfig();

	@Subscribe
	public void onCommandExecute(CommandExecuteEvent event) {
		if (config.isLogPlayerCommand()) FutureUtil.thenRun(() -> onCommandExecuteImpl(event));
	}

	private void onCommandExecuteImpl(CommandExecuteEvent event) {
		if (event.getCommandSource() instanceof Player player) {
			player.getCurrentServer().ifPresent(server -> COMMAND_LOGGER.info("[{}]<{}> /{}",
					server.getServer().getServerInfo().getName(),
					player.getUsername(),
					event.getCommand()));
		}
	}
}