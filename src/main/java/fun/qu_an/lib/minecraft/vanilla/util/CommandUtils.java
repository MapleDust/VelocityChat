package fun.qu_an.lib.minecraft.vanilla.util;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fun.qu_an.lib.basic.util.CollectionUtils;
import fun.qu_an.lib.basic.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CommandUtils {
	public static final String[] EXECUTE = {"execute"};

	public static int indexOfNode(@NotNull List<String> command, String @NotNull ... nodes) {
		for (String node : nodes) {
			int i = command.indexOf(node);
			if (i >= 0) return i;
		}
		return -1;
	}

	public static int indexOfRoot(@NotNull List<String> command, String @NotNull ... roots) {
		for (String root : roots) {
			String s0 = command.get(0);
			if (s0.equals(root)) return command.indexOf(root);
			if (TextUtils.equalsAny(s0, EXECUTE)
				&& command.contains(root)) {
				return command.indexOf(root);
			}
		}
		return -1;
	}

	public static boolean is(@NotNull List<String> command, String @NotNull ... roots) {
		for (String root : roots) {
			String s0 = command.get(0);
			if (s0.equals(root)) return true;
			if (TextUtils.equalsAny(s0, EXECUTE)
				&& command.contains(root)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 构建命令提示
	 *
	 * @param builder     命令提示构建器
	 * @param collection1 候选内容
	 * @param collections 候选内容
	 * @return 包含所有命令提示的计划任务
	 */
	@SafeVarargs
	public static CompletableFuture<Suggestions> buildSuggestions(@NotNull SuggestionsBuilder builder, Collection<String> collection1, Collection<String>... collections) {
		String input = builder.getRemaining().toLowerCase(Locale.ROOT);
		collection1 = CollectionUtils.mixToSet(collection1, collections);
		for (String candidate : collection1) {
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
	public static boolean shouldSuggest(String input, @NotNull String full) {
		int i = 0;
		while (!full.startsWith(input, i)) {
			if ((i = full.indexOf('_', i)) < 0) {
				return false;
			}
			++i;
		}
		return true;
	}
}
