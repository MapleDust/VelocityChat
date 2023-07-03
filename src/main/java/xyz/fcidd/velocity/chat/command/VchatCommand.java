package xyz.fcidd.velocity.chat.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.text.Translates;
import xyz.fcidd.velocity.chat.util.Utils;

import java.util.Optional;

import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.text.Translates.PLAYER_ONLY;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class VchatCommand {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	private static final RequiredArgumentBuilder<CommandSource, String> broadcastMessageNode = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeBroadcast);
	private static final RequiredArgumentBuilder<CommandSource, String> localMessageNode = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeLocal);
	private static CommandMeta broadcastMeta;
	private static CommandMeta localMeta;
	private static final String PERMISSION_VELOCITY_RELOAD = "velocity.command.reload";

	public static void register() {
		CommandManager commandManager = PROXY_SERVER.getCommandManager();
		commandManager.register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("vchat")
			.then(getBroadcast("broadcast"))
			.then(getLocal("local"))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("help")
				.executes(VchatCommand::executeHelp))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("reload")
				.requires(commandSource -> commandSource.hasPermission(PERMISSION_VELOCITY_RELOAD))
				.executes(VchatCommand::executeReload))));
		reloadAlias();
	}

	public static void reloadAlias() {
		CommandManager commandManager = PROXY_SERVER.getCommandManager();

		if (broadcastMeta != null) {
			commandManager.unregister(broadcastMeta);
		}
		if (localMeta != null) {
			commandManager.unregister(localMeta);
		}

		String commandBroadcastAlias = CONFIG.getCommandBroadcastAlias();
		BrigadierCommand broadcastCommand;
		if (!commandManager.hasCommand(commandBroadcastAlias)) {
			broadcastCommand = new BrigadierCommand(getBroadcast(commandBroadcastAlias));
			CommandMeta broadcastMeta = commandManager.metaBuilder(broadcastCommand).build();
			commandManager.register(broadcastMeta, broadcastCommand);
			VchatCommand.broadcastMeta = broadcastMeta;
		} else {
			logger.warn("Command {} is occupied!", commandBroadcastAlias);
		}

		String commandLocalAlias = CONFIG.getCommandLocalAlias();
		BrigadierCommand localCommand;
		if (!commandManager.hasCommand(commandLocalAlias)) {
			localCommand = new BrigadierCommand(getLocal(commandLocalAlias));
			CommandMeta localMeta = commandManager.metaBuilder(localCommand).build();
			commandManager.register(localMeta, localCommand);
			VchatCommand.localMeta = localMeta;
		} else {
			logger.warn("Command {} is occupied!", commandLocalAlias);
		}
	}

	private static LiteralArgumentBuilder<CommandSource> getLocal(String alias) {
		return LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof Player)
			.then(localMessageNode);
	}

	private static LiteralArgumentBuilder<CommandSource> getBroadcast(String alias) {
		return LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.then(broadcastMessageNode);
	}

	private static int executeLocal(CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		CommandSource source = context.getSource();
		if (!(source instanceof Player player)) {
			source.sendMessage(PLAYER_ONLY);
			return 0;
		}
		Optional<ServerConnection> currentServer = player.getCurrentServer();
		if (currentServer.isEmpty()) {
			player.sendMessage(Translates.SERVER_NOT_FOUND);
			return 0;
		}
		player.spoofChatInput(message);
		return 1;
	}

	private static int executeBroadcast(CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		CommandSource source = context.getSource();
		if (source instanceof Player player) {
			Utils.sendGlobalPlayerChat(player, message);
		} else if (source instanceof ConsoleCommandSource) {
			PROXY_SERVER.sendMessage(Component.text("§4[Proxy] §r").append(Component.text(message)));
		}
		return 1;
	}

	private static int executeReload(CommandContext<CommandSource> context) {
		VelocityChatPlugin.getInstance().onProxyReload(null);
		return 1;
	}

	private static int executeHelp(CommandContext<CommandSource> context) {
		context.getSource().sendMessage(Translates.HELP
			.append(Component.newline())
			.append(Translates.DASH_AND_SPACE)
			.append(Translates.HELP_BROADCAST
				.args(Component.text(CONFIG.getCommandBroadcastAlias())))
			.append(Component.newline())
			.append(Translates.DASH_AND_SPACE)
			.append(Translates.HELP_LOCAL
				.args(Component.text(CONFIG.getCommandLocalAlias()))));
		return 1;
	}
}
