package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.fcidd.velocity.chat.util.FutureUtil;

import static xyz.fcidd.velocity.chat.config.ConfigManager.CONFIG;
import static xyz.fcidd.velocity.chat.util.PluginUtil.LOGGER;

public class CommandExecuteListener {
	@Subscribe
	public void onCommandExecute(CommandExecuteEvent event) {
		if (CONFIG.isLogPlayerCommand()) FutureUtil.thenRun(() -> onCommandExecuteImpl(event));
	}

	private void onCommandExecuteImpl(CommandExecuteEvent event) {
		if (event.getCommandSource() instanceof Player player) {
			player.getCurrentServer().ifPresent(server -> LOGGER.info("[cmd][{}]<{}> /{}",
					server.getServer().getServerInfo().getName(),
					player.getUsername(),
					event.getCommand()));
		}
	}
}
