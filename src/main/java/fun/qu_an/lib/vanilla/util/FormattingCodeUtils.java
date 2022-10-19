package fun.qu_an.lib.vanilla.util;

import org.jetbrains.annotations.NotNull;

public class FormattingCodeUtils {
	// 颜色代码：{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

	/**
	 * 将使用“&”的格式化代码中的“&”替换为“§”
	 *
	 * @param raw 待替换的字符串
	 * @return 替换后的字符串
	 */
	public static @NotNull String replaceFormattingChar(@NotNull String raw) {
		return raw.contains("&") ? raw.replaceAll("&(?=[0-9a-fk-or])", "§") : raw;
	}
}
