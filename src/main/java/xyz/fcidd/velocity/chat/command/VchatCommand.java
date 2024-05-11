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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;
import xyz.fcidd.velocity.chat.message.MessageChannel;
import xyz.fcidd.velocity.chat.util.Caches;
import xyz.fcidd.velocity.chat.util.CommandUtils;
import xyz.fcidd.velocity.chat.util.ComponentUtils;
import xyz.fcidd.velocity.chat.util.Utils;

import java.util.Optional;
import java.util.function.Function;

import static xyz.fcidd.velocity.chat.command.Commands.*;
import static xyz.fcidd.velocity.chat.config.VelocityChatConfig.CONFIG;
import static xyz.fcidd.velocity.chat.message.Components.*;
import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class VchatCommand {
	private static final Logger logger = VelocityChatPlugin.getLogger();
	private static CommandMeta globalMeta;
	private static CommandMeta localMeta;
	private static CommandMeta broadcastMeta;
	private static CommandMeta tellMeta;
	private static CommandMeta tellconsoleMeta;
	private static CommandMeta notifyMeta;
	private static final String PERMISSION_VELOCITY_ADMIN = "velocity.command.admin";
	private static final RequiredArgumentBuilder<CommandSource, String> GLOBAL_MESSAGE_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeGlobalMessage);
	private static final RequiredArgumentBuilder<CommandSource, String> LOCAL_MESSAGE_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeLocalMessage);
	private static final RequiredArgumentBuilder<CommandSource, String> TELL_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("player", StringArgumentType.word())
		.suggests(CommandUtils::suggestPlayers)
		.then(RequiredArgumentBuilder
			.<CommandSource, String>argument("message", StringArgumentType.greedyString())
			.executes(VchatCommand::executeTell));
	private static final RequiredArgumentBuilder<CommandSource, String> BROADCAST_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeBroadcast);
	private static final RequiredArgumentBuilder<CommandSource, String> TELLCONSOLE_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("message", StringArgumentType.greedyString())
		.executes(VchatCommand::executeTellConsole);
	private static final RequiredArgumentBuilder<CommandSource, String> NOTIFY_RRB = RequiredArgumentBuilder
		.<CommandSource, String>argument("server", StringArgumentType.word())
		.suggests(CommandUtils::suggestServers)
		.then(RequiredArgumentBuilder
			.<CommandSource, String>argument("message", StringArgumentType.greedyString())
			.executes(VchatCommand::executeNotify));

	public static void register() {
		PROXY_SERVER.getCommandManager().register(new BrigadierCommand(LiteralArgumentBuilder
			.<CommandSource>literal("vchat")
			.executes(VchatCommand::executeHelp)
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("help")
				.executes(VchatCommand::executeHelp))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("channel")
				.requires(commandSource -> commandSource instanceof Player)
				.executes(VchatCommand::executeChannel))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(GLOBAL)
				.executes(VchatCommand::executeGlobal)
				.requires(commandSource -> commandSource instanceof Player)
				.then(GLOBAL_MESSAGE_RRB))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(LOCAL)
				.executes(VchatCommand::executeLocal)
				.requires(commandSource -> commandSource instanceof Player)
				.then(LOCAL_MESSAGE_RRB))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal("reload")
				.requires(commandSource -> commandSource.hasPermission(PERMISSION_VELOCITY_ADMIN))
				.executes(VchatCommand::executeReload))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(BROADCAST)
				.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
				.then(BROADCAST_RRB))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(TELL)
				.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
				.then(TELL_RRB))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(TELLCONSOLE)
				.requires(commandSource -> commandSource instanceof Player)
				.then(TELLCONSOLE_RRB))
			.then(LiteralArgumentBuilder
				.<CommandSource>literal(NOTIFY)
				.requires(commandSource -> commandSource.hasPermission(PERMISSION_VELOCITY_ADMIN))
				.then(NOTIFY_RRB))));
		reload();
	}

	public static void reload() {

		// global

		globalMeta = reloadAlias(CONFIG.getCommandGlobalAlias(), globalMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof Player)
			.executes(VchatCommand::executeGlobal)
			.then(GLOBAL_MESSAGE_RRB));

		// local

		localMeta = reloadAlias(CONFIG.getCommandLocalAlias(), localMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof Player)
			.executes(VchatCommand::executeLocal)
			.then(LOCAL_MESSAGE_RRB));

		// broadcast

		broadcastMeta = reloadAlias(CONFIG.getCommandBroadcastAlias(), broadcastMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
			.then(BROADCAST_RRB));

		// tell

		tellMeta = reloadAlias(CONFIG.getCommandTellAlias(), tellMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof ConsoleCommandSource)
			.then(TELL_RRB));

		// tellconsole

		tellconsoleMeta = reloadAlias(CONFIG.getCommandTellconsoleAlias(), tellconsoleMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(alias)
			.requires(commandSource -> commandSource instanceof Player)
			.then(TELLCONSOLE_RRB));

		// notify

		notifyMeta = reloadAlias(CONFIG.getCommandNotifyAlias(), notifyMeta, alias -> LiteralArgumentBuilder
			.<CommandSource>literal(NOTIFY)
			.requires(commandSource -> commandSource.hasPermission(PERMISSION_VELOCITY_ADMIN))
			.then(NOTIFY_RRB));
	}

	private static CommandMeta reloadAlias(String alias, CommandMeta meta, Function<String, LiteralArgumentBuilder<CommandSource>> commandSupplier) {
		CommandManager commandManager = PROXY_SERVER.getCommandManager();
		if (meta != null) {
			commandManager.unregister(meta);
		}
		if (commandManager.hasCommand(alias)) {
			logger.warn("Command alias {} is occupied!", alias);
			return null;
		}
		BrigadierCommand command = new BrigadierCommand(commandSupplier.apply(alias));
		meta = commandManager.metaBuilder(command).aliases(alias).build();
		commandManager.register(meta, command);
		return meta;
	}

	private static int executeLocalMessage(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Player player = (Player) context.getSource();
		Optional<ServerConnection> currentServer = player.getCurrentServer();
		if (currentServer.isEmpty()) {
			player.sendMessage(HAS_NO_SERVER);
			return 0;
		}
		player.spoofChatInput(message);
		return 1;
	}

	private static int executeGlobalMessage(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isFormattableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		CommandSource source = context.getSource();
		Utils.sendGlobalPlayerChat(((Player) source), messageComponent);
		return 1;
	}

	private static int executeChannel(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		MessageChannel channel = Caches.getPlayerChannel(player);
		if (channel == null) {
			channel = CONFIG.getDefaultMessageChannel();
		}
		switch (channel) {
			case GLOBAL -> player.sendMessage(CHANNEL_CURRENT.args(CHANNEL_GLOBAL));
			case LOCAL -> player.sendMessage(CHANNEL_CURRENT.args(CHANNEL_LOCAL));
		}
		return 1;
	}

	private static int executeGlobal(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		Caches.setPlayerChannel(player, MessageChannel.GLOBAL);
		player.sendMessage(CHANNEL_SWITCH.args(CHANNEL_GLOBAL));
		return 1;
	}

	private static int executeLocal(CommandContext<CommandSource> context) {
		Player player = (Player) context.getSource();
		Caches.setPlayerChannel(player, MessageChannel.LOCAL);
		player.sendMessage(CHANNEL_SWITCH.args(CHANNEL_LOCAL));
		return 1;
	}

	private static int executeReload(CommandContext<CommandSource> context) {
		VelocityChatPlugin.reload();
		PROXY_SERVER.sendMessage(RELOADED);
		return 1;
	}

	private static int executeHelp(@NotNull CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();

		Component message = HELP
			.appendNewline()
			.append(DASH_AND_SPACE)
			.append(HELP_FORMATTABLE_CHAT);

		if (source instanceof Player player) {
			if (player.hasPermission(PERMISSION_VELOCITY_ADMIN)) {
				message = message.appendNewline()
					.append(DASH_AND_SPACE)
					.append(HELP_RELOAD).appendNewline()
					.append(DASH_AND_SPACE)
					.append(HELP_NOTIFY.args(Component.text(CONFIG.getCommandNotifyAlias())));
			}
			source.sendMessage(message
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_CHANNEL_GLOBAL.args(Component.text(CONFIG.getCommandGlobalAlias())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_CHANNEL_LOCAL.args(Component.text(CONFIG.getCommandLocalAlias())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_GLOBAL.args(Component.text(CONFIG.getCommandGlobalAlias()), Component.text(CONFIG.getGlobalChatPrefix())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_LOCAL.args(Component.text(CONFIG.getCommandLocalAlias()), Component.text(CONFIG.getLocalChatPrefix())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_TELLCONSOLE.args(Component.text(CONFIG.getCommandTellconsoleAlias())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_CHANNEL));
			return 1;
		}

		if (source instanceof ConsoleCommandSource console) {
			console.sendMessage(message
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_RELOAD)
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_BROADCAST.args(Component.text(CONFIG.getCommandBroadcastAlias())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_NOTIFY.args(Component.text(CONFIG.getCommandNotifyAlias())))
				.appendNewline()
				.append(DASH_AND_SPACE)
				.append(HELP_TELL.args(Component.text(CONFIG.getCommandTellAlias()))));
			return 1;
		}

		return 0;
	}

	private static int executeTell(CommandContext<CommandSource> context) {
		String player = context.getArgument("player", String.class);
		String message = context.getArgument("message", String.class);
		CommandSource source = context.getSource();
		// 发送私聊
		PROXY_SERVER.getPlayer(player).ifPresentOrElse(player1 -> {
			Component messageComponent;
			if (CONFIG.isFormattableChat()) {
				messageComponent = ComponentUtils.formattedMessage(message);
			} else {
				messageComponent = Component.text(message);
			}
			player1.sendMessage(TELL_MESSAGE.args(
				PROXY,
				messageComponent
			));
			// 发送反馈
			source.sendMessage(TELL_SUCCEED.args(
				ComponentUtils.getPlayerComponent(player1),
				messageComponent
			));
		}, () -> {
			// 发送反馈
			source.sendMessage(PLAYER_NOT_FOUND.args(Component.text(player)));
		});
		return 1;
	}

	private static int executeBroadcast(@NotNull CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isFormattableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		PROXY_SERVER.sendMessage(PROXY_BROADCAST_PREFIX.append(messageComponent));
		return 1;
	}

	private static int executeTellConsole(CommandContext<CommandSource> context) {
		String message = context.getArgument("message", String.class);
		Component messageComponent;
		if (CONFIG.isFormattableChat()) {
			messageComponent = ComponentUtils.formattedMessage(message);
		} else {
			messageComponent = Component.text(message);
		}
		Player source = (Player) context.getSource();
		PROXY_SERVER.getConsoleCommandSource().sendMessage(TELL_MESSAGE.args(
			ComponentUtils.getPlayerComponent(source),
			messageComponent
		));
		source.sendMessage(TELL_SUCCEED.args(
			PROXY,
			messageComponent
		));
		return 1;
	}

	private static int executeNotify(CommandContext<CommandSource> context) {
		String server = context.getArgument("server", String.class);
		String message = context.getArgument("message", String.class);
		CommandSource source = context.getSource();
		// 发送通知
		PROXY_SERVER.getServer(server).ifPresentOrElse(target1 -> {
			Component message0;
			if (CONFIG.isFormattableChat()) {
				message0 = ComponentUtils.formattedMessage(message);
			} else {
				message0 = Component.text(message);
			}
			Component message1;
			if (source instanceof ConsoleCommandSource) {
				message1 = PROXY;
			} else if (source instanceof Player player) {
				message1 = ComponentUtils.getPlayerComponent(player);
			} else {
				message1 = Component.text("???"); // ???为什么会进这里？
			}
			message1 = message1.append(PROXY_NOTIFY_0)
				.append(ComponentUtils.getServerComponent(target1))
				.append(PROXY_NOTIFY_1)
				.append(message0);
			// 发送
			target1.sendMessage(message1);
			// 反馈
			source.sendMessage(message1);
		}, () -> {
			// 发送反馈
			source.sendMessage(SERVER_NOT_FOUND.args(Component.text(server)));
		});
		return 1;
	}
}
