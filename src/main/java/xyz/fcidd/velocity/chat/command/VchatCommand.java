package xyz.fcidd.velocity.chat.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.MessageChannel;
import xyz.fcidd.velocity.chat.message.Translates;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.CommandUtils;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.util.Utils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static xyz.fcidd.velocity.chat.command.Commands.*;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class VchatCommand {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	private static CommandMeta globalMeta;
	private static CommandMeta localMeta;
	private static CommandMeta broadcastMeta;
	private static BrigadierCommand broadcastCommand;
	private static final String PERMISSION_VELOCITY_RELOAD = "velocity.command.admin";

	public static void register() {
		CommandManager commandManager = PROXY_SERVER.getCommandManager();

		BrigadierCommand broadcastCommand = new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal(BROADCAST)
			.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
			.then(RequiredArgumentBuilder
				.<CommandSource, String>argument("message", StringArgumentType.greedyString())
				.executes(VchatCommand::executeConsoleBroadcast)));
		VchatCommand.broadcastCommand = broadcastCommand;
		commandManager.register(broadcastCommand);

		// 控制台tell指令
		commandManager.register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("tell")
			.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
			.then(RequiredArgumentBuilder
				.<CommandSource, String>argument("player", StringArgumentType.word())
				.suggests(VchatCommand::suggestPlayers)
				.then(RequiredArgumentBuilder
					.<CommandSource, String>argument("message", StringArgumentType.greedyString())
					.executes(VchatCommand::executeTell)))));

		// 向控制台私信 tellconsole
		commandManager.register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("tellconsole")
			.requires(commandSource -> commandSource instanceof Player)
			.then(RequiredArgumentBuilder
				.<CommandSource, String>argument("message", StringArgumentType.greedyString())
				.executes(VchatCommand::executeTellConsole))));

		// 控制台tell指令
		commandManager.register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("tell")
			.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
			.then(RequiredArgumentBuilder
				.<CommandSource, String>argument("player", StringArgumentType.word())
				.suggests(VchatCommand::suggestPlayers)
				.then(RequiredArgumentBuilder
					.<CommandSource, String>argument("message", StringArgumentType.greedyString())
					.executes(VchatCommand::executeTell)))));

		commandManager.register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("vchat")
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("help")
				.executes(VchatCommand::executeHelp))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("channel")
				.requires(commandSource -> commandSource instanceof Player)
				.executes(VchatCommand::executeChannel)
				.then(LiteralArgumentBuilder
					.<CommandSource>literal(GLOBAL)
					.executes(VchatCommand::executeChannelGlobal))
				.then(LiteralArgumentBuilder
					.<CommandSource>literal(LOCAL)
					.executes(VchatCommand::executeChannelLocal)))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(GLOBAL)
				.requires(commandSource -> commandSource instanceof Player)
				.then(RequiredArgumentBuilder
					.<CommandSource, String>argument("message", StringArgumentType.greedyString())
					.executes(VchatCommand::executeGlobalMessage)))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(LOCAL)
				.requires(commandSource -> commandSource instanceof Player)
				.then(RequiredArgumentBuilder
					.<CommandSource, String>argument("message", StringArgumentType.greedyString())
					.executes(VchatCommand::executeLocalMessage)))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("reload")
				.requires(commandSource -> commandSource.hasPermission(PERMISSION_VELOCITY_RELOAD))
				.executes(VchatCommand::executeReload))));
		reloadAlias();
	}

	public static void reloadAlias() {
		// 重载命令别名
		CommandManager commandManager = PROXY_SERVER.getCommandManager();

		if (globalMeta != null) {
			commandManager.unregister(globalMeta);
		}
		if (localMeta != null) {
			commandManager.unregister(localMeta);
		}
		if (broadcastMeta != null) {
			commandManager.unregister(broadcastMeta);
		}

		String commandGlobalAlias = CONFIG.getCommandGlobalAlias();
		BrigadierCommand globalCommand;
		if (!commandManager.hasCommand(commandGlobalAlias)) {
			globalCommand = new BrigadierCommand(LiteralArgumentBuilder
				.<CommandSource>literal(commandGlobalAlias)
				.requires(commandSource -> commandSource instanceof Player)
				.executes(VchatCommand::executeChannelGlobal));
			CommandMeta globalMeta = commandManager.metaBuilder(globalCommand).build();
			commandManager.register(globalMeta, globalCommand);
			VchatCommand.globalMeta = globalMeta;
		} else {
			logger.warn("Command alias {} is occupied!", commandGlobalAlias);
		}

		String commandLocalAlias = CONFIG.getCommandLocalAlias();
		BrigadierCommand localCommand;
		if (!commandManager.hasCommand(commandLocalAlias)) {
			localCommand = new BrigadierCommand(LiteralArgumentBuilder
				.<CommandSource>literal(commandLocalAlias)
				.requires(commandSource -> commandSource instanceof Player)
				.executes(VchatCommand::executeChannelLocal));
			CommandMeta localMeta = commandManager.metaBuilder(localCommand).build();
			commandManager.register(localMeta, localCommand);
			VchatCommand.localMeta = localMeta;
		} else {
			logger.warn("Command alias {} is occupied!", commandLocalAlias);
		}

		String commandBroadcastAlias = CONFIG.getCommandBroadcastAlias();
		if (!commandManager.hasCommand(commandBroadcastAlias)) {
			BrigadierCommand broadcastCommand = VchatCommand.broadcastCommand;
			CommandMeta broadcastMeta = commandManager.metaBuilder(broadcastCommand).aliases(commandBroadcastAlias).build();
			commandManager.register(broadcastMeta, broadcastCommand);
			VchatCommand.broadcastMeta = broadcastMeta;
		} else {
			logger.warn("Command alias {} is occupied!", commandBroadcastAlias);
		}
	}

	private static int executeLocalMessage(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Player player = (Player) context.getSource();
		Optional<ServerConnection> currentServer = player.getCurrentServer();
		if (currentServer.isEmpty()) {
			player.sendMessage(Translates.SERVER_NOT_FOUND);
			return 0;
		}
		player.spoofChatInput(message);
		return 1;
	}

	private static int executeGlobalMessage(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isColorableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		CommandSource source = context.getSource();
		Utils.sendGlobalPlayerChat(((Player) source), messageComponent);
		return 1;
	}

	private static int executeConsoleBroadcast(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isColorableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		PROXY_SERVER.sendMessage(Translates.PROXY_BROADCAST_PREFIX.append(messageComponent));
		return 1;
	}

	private static int executeChannel(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		MessageChannel channel = Caches.getPlayerChannel(player);
		if (channel == null) {
			channel = CONFIG.getDefaultMessageChannel();
		}
		switch (channel) {
			case GLOBAL -> player.sendMessage(Translates.CHANNEL_CURRENT.args(Translates.CHANNEL_GLOBAL));
			case LOCAL -> player.sendMessage(Translates.CHANNEL_CURRENT.args(Translates.CHANNEL_LOCAL));
		}
		return 1;
	}

	private static int executeChannelGlobal(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		Caches.setPlayerChannel(player, MessageChannel.GLOBAL);
		player.sendMessage(Translates.CHANNEL_SWITCH.args(Translates.CHANNEL_GLOBAL));
		return 1;
	}

	private static int executeChannelLocal(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		Caches.setPlayerChannel(player, MessageChannel.LOCAL);
		player.sendMessage(Translates.CHANNEL_SWITCH.args(Translates.CHANNEL_LOCAL));
		return 1;
	}

	private static int executeReload(CommandContext<CommandSource> context) {
		VelocityChatPlugin.reload();
		PROXY_SERVER.sendMessage(Translates.RELOADED);
		return 1;
	}

	private static int executeHelp(@NotNull CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();

		if (source instanceof Player player) {
			Component message = Translates.HELP;
			if (player.hasPermission(PERMISSION_VELOCITY_RELOAD)) {
				message = message.appendNewline()
					.append(Translates.DASH_AND_SPACE)
					.append(Translates.HELP_RELOAD);
			}
			message = message
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_GLOBAL
					.args(Component.text(CONFIG.getGlobalChatPrefix())))
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_LOCAL
					.args(Component.text(CONFIG.getLocalChatPrefix())))
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_CHANNEL)
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_CHANNEL_GLOBAL
					.args(Component.text(CONFIG.getCommandGlobalAlias())))
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_CHANNEL_LOCAL
					.args(Component.text(CONFIG.getCommandLocalAlias())));
			source.sendMessage(message);
			return 1;
		}

		if (source instanceof ConsoleCommandSource console) {
			console.sendMessage(Translates.HELP
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_BROADCAST.args(Component.text(CONFIG.getCommandBroadcastAlias())))
				.appendNewline()
				.append(Translates.DASH_AND_SPACE)
				.append(Translates.HELP_RELOAD));
			return 1;
		}

		return 0;
	}

	private static int executeTell(CommandContext<CommandSource> context) {
		String player = context.getArgument("player", String.class);
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isColorableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		tell(player, messageComponent, context.getSource());
		return 1;
	}

	private static int executeTellConsole(CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isColorableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		PROXY_SERVER.getConsoleCommandSource().sendMessage(Translates.TELL_MESSAGE.args(
			ComponentUtils.getPlayerComponent((Player) context.getSource()),
			messageComponent
		));
		return 1;
	}

	private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
		return CommandUtils.buildSuggestions(builder, PROXY_SERVER.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toSet()));
	}
}
