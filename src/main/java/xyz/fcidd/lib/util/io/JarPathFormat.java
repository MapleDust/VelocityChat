package xyz.fcidd.lib.util.io;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * 格式化Jar内路径字符串<p>
 * 仅用于在jar文件内部寻找资源时规范路径格式<p>
 * 一般情况下应尽可能使用{@link java.nio.file.Path}替代
 */
@SuppressWarnings("unused")
public enum JarPathFormat {
	ENDS_WITH_SLASH(rawPath -> {
		if (rawPath.endsWith("/")) {
			return rawPath;
		}
		return rawPath + "/";
	}),
	ENDS_WITHOUT_SLASH(rawPath -> {
		if (rawPath.endsWith("/")) {
			return rawPath.substring(0, rawPath.length() - 1);
		}
		return rawPath;
	}),
	STARTS_WITH_SLASH(rawPath -> {
		if (rawPath.startsWith("/")) {
			return rawPath;
		}
		return "/" + rawPath;
	}),
	STARTS_WITHOUT_SLASH(rawPath -> {
		if (rawPath.startsWith("/")) {
			return rawPath.substring(1);
		}
		return rawPath;
	});
	private final Function<String, String> formatter;

	JarPathFormat(Function<String, String> formatter) {
		this.formatter = formatter;
	}

	public static String format(String rawPath, JarPathFormat @NotNull ... formats) {
		rawPath = rawPath.replace('\\', '/');
		for (JarPathFormat format : formats) {
			rawPath = format.formatter.apply(rawPath);
		}
		return rawPath;
	}
}
