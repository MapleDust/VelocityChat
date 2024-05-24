package xyz.fcidd.velocity.chat.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class Commands {
	public static final String[] TELLs = {"tell", "msg"};
	public static final String[] TELEPORTs = {"tp", "teleport"};
	public static final String GLOBAL = "global";
	public static final String LOCAL = "local";
	public static final String BROADCAST = "broadcast";
	public static final String TELL = "tell";
	public static final String TELLCONSOLE = "tellconsole";
	public static final String NOTIFY = "notify";
	public static final String GLOBAL_DEFAULT_ALIAS = "glb";
	public static final String LOCAL_DEFAULT_ALIAS = "lcl";
	public static final String BROADCAST_DEFAULT_ALIAS = "br";
	public static final String EXECUTE = "execute";
	public static final String RUN = "run";
	public static final int SUCCEED = 1;
	public static final int FAILED = 0;

	/**
	 * 构建命令提示
	 *
	 * @param builder    命令提示构建器
	 * @param collection 候选内容
	 * @return 包含所有命令提示的计划任务
	 */
	public static CompletableFuture<Suggestions> buildSuggestions(@NotNull SuggestionsBuilder builder, @NotNull Collection<String> collection) {
		String input = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (String candidate : collection) {
			if (shouldSuggest(input, candidate)) {
				builder.suggest(candidate);
			}
		}

		return builder.buildFuture();
	}

	/**
	 * 判断输入的字符串是否匹配待匹配的字符串
	 *
	 * @param input 输入的字符串
	 * @param full  完整的待匹配字符串
	 * @return 匹配则返回 true，否则返回 false
	 */
	public static boolean shouldSuggest(@NotNull String input, @NotNull String full) {
		int i = 0;
		while (!full.startsWith(input, i)) {
			if ((i = full.indexOf('_', i)) < 0) {
				return false;
			}
			++i;
		}

		return true;
	}

	public static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
		return buildSuggestions(builder, PROXY_SERVER.getAllPlayers().stream()
			.map(Player::getUsername)
			.collect(Collectors.toSet()));
	}

	public static CompletableFuture<Suggestions> suggestServers(CommandContext<CommandSource> commandSourceCommandContext, SuggestionsBuilder builder) {
		return buildSuggestions(builder, PROXY_SERVER.getAllServers().stream()
			.map(server -> server.getServerInfo().getName())
			.collect(Collectors.toSet()));
	}
}
