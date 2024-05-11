package xyz.fcidd.lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.fcidd.lib.util.reflect.FieldAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class AnnotationConfigUtils {
	private static final Map<String, String> CONFIG_KEY_CACHE = new WeakHashMap<>();

	public static @Nullable String getString(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static @Nullable Boolean getBoolean(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static <T> @Nullable List<T> getList(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static @Nullable <T extends Config> T getTable(@NotNull T config, String path) {
		try {
			return config.get(path);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static @NotNull CommentedConfig wrap(@NotNull Map<?, ?> tree) {
		return wrap(tree, false);
	}

	public static @NotNull CommentedConfig wrap(@NotNull Map<?, ?> tree, boolean concurrent) {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<?, ?> entry : tree.entrySet()) {
			Object o = entry.getKey();
			if (!(o instanceof String key)) {
				throw new IllegalStateException("Unexpected key: " + o);
			}
			Object value = entry.getValue();
			if (value instanceof Boolean || value instanceof Number || value instanceof String || value instanceof List<?>) {
				map.put(key, value);
				continue;
			}
			if (value instanceof Map<?, ?> valueMap) {
				map.put(key, wrap(valueMap, concurrent));
			}
			throw new IllegalStateException("Unexpected value: " + value);
		}
		if (concurrent) {
			return CommentedConfig.wrap(map, CommentedConfig.inMemoryConcurrent().configFormat());
		}
		return CommentedConfig.wrap(map, CommentedConfig.inMemory().configFormat());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static GenericBuilder<CommentedConfig, CommentedFileConfig> defaultConfigBuilder(@NotNull Path path) {
		return CommentedFileConfig
			.builder(path)
//				.autosave() // 自动保存
			.concurrent() // 线程安全
			.onFileNotFound(((file, configFormat) -> {
				Path parent = file.getParent();
				if (parent != null) {
					parent.toFile().mkdirs(); // 创建父目录
				}
				file.toFile().createNewFile(); // 创建文件
				configFormat.initEmptyFile(file); // 获取文件格式
				return false; // 阻断后续操作，因为文件为空
			}))
			.preserveInsertionOrder() // 保持顺序
			.charset(StandardCharsets.UTF_8)
			.parsingMode(ParsingMode.MERGE)
			.writingMode(WritingMode.REPLACE);
	}

	public static @NotNull List<ConfigFieldRecord> getConfigFields(@NotNull AnnotationConfig annotationConfig) {
		List<ConfigFieldRecord> list = new ArrayList<>();
		for (Field field : annotationConfig.getClass().getDeclaredFields()) {
			int modifiers = field.getModifiers();
			// 检查是否static
			boolean isStatic = Modifier.isStatic(modifiers);
			if (!field.isAnnotationPresent(ConfigKey.class)
				|| Modifier.isTransient(modifiers)) {
				continue;
			}
			ConfigKey annotation = field.getAnnotation(ConfigKey.class);
			// 获取路径，不存在则默认为根据变量名生成
			String path = annotation.path();
			if ("".equals(path)) {
				path = getTomlKey(field.getName());
			}
			list.add(new ConfigFieldRecord(new FieldAccessor(annotationConfig, field), path, annotation.comment()));
		}
		return list;
	}

	private static @NotNull String getTomlKey(@NotNull String fieldName) {
		String tomlPath = CONFIG_KEY_CACHE.get(fieldName);
		if (tomlPath == null) {
			StringBuilder sb = new StringBuilder();
			for (char c : fieldName.toCharArray()) {
				if (Character.isUpperCase(c)) {
					sb.append('_').append(Character.toLowerCase(c));
				} else {
					sb.append(c);
				}
			}
			tomlPath = sb.toString();
			CONFIG_KEY_CACHE.put(fieldName, tomlPath);
		}
		return tomlPath;
	}
}
