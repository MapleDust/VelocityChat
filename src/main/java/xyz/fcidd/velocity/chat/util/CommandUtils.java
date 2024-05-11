package xyz.fcidd.velocity.chat.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

/**
 * 游戏命令相关工具
 */
@SuppressWarnings("unused")
public class CommandUtils {
	public static final String EXECUTE = "execute";
	public static final String RUN = "run";
	public static final int SUCCEED = 1;
	public static final int FAILED = 0;

	/**
	 * 获取根命令节点在命令中第一次出现的节点索引，一般用于判断 execute 指令中使用的根命令的索引
	 *
	 * @param command 包含每个命令节点的列表
	 * @param roots   根命令节点
	 * @return 命令中没有指定节点时返回 -1 ，否则返回指定节点在命令节点中第一次出现时的节点索引
	 */
	public static int indexOfRoot(@NotNull List<String> command, String @NotNull ... roots) {
		String s0 = command.get(0);

		if (s0.equals(EXECUTE)) {
			for (String root : roots) {
				int index = command.indexOf(root);
				if (command.indexOf(RUN) == index - 1) {
					return index;
				}
			}
		} else if (CharacterUtils.equalsAny(s0, roots)) {
			return 0;
		}

		return -1;
	}

	/**
	 * 构建命令提示
	 *
	 * @param builder 命令提示构建器
	 * @param set     候选内容
	 * @return 包含所有命令提示的计划任务
	 */
	public static CompletableFuture<Suggestions> buildSuggestions(@NotNull SuggestionsBuilder builder, @NotNull Set<String> set) {
		String input = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (String candidate : set) {
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
		return CommandUtils.buildSuggestions(builder, PROXY_SERVER.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toSet()));
	}

	public static CompletableFuture<Suggestions> suggestServers(CommandContext<CommandSource> commandSourceCommandContext, SuggestionsBuilder builder) {
		return CommandUtils.buildSuggestions(builder, PROXY_SERVER.getAllServers().stream().map(server -> server.getServerInfo().getName()).collect(Collectors.toSet()));
	}
}
