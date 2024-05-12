package xyz.fcidd.lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fcidd.lib.util.reflect.FieldAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class AnnotationConfigUtils {
	private static final Logger logger = LoggerFactory.getLogger(AnnotationConfig.class.getSimpleName());
	private static final Map<String, String> CONFIG_KEY_CACHE = new WeakHashMap<>();
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

	public static GenericBuilder<CommentedConfig, CommentedFileConfig> defaultConfigBuilder(@NotNull Path path) {
		return CommentedFileConfig
			.builder(path)
//				.autosave() // 自动保存
			.concurrent() // 线程安全
			.onFileNotFound(((path1, configFormat) -> {
				Files.createDirectories(path1.getParent());
				Files.createFile(path1);
				configFormat.initEmptyFile(path1); // 获取文件格式
				return false; // 阻断后续操作，因为文件为空
			}))
			.preserveInsertionOrder() // 保持顺序
			.charset(StandardCharsets.UTF_8)
			.parsingMode(ParsingMode.MERGE)
			.writingMode(WritingMode.REPLACE);
	}

	public static @NotNull List<ConfigFieldRecord> getConfigFields(@NotNull AnnotationConfig annotationConfig) {
		List<ConfigFieldRecord> list = new ArrayList<>();
		Set<String> pathSet = new HashSet<>();
		Set<String> commentPathSet = new HashSet<>();
		Class<? extends @NotNull AnnotationConfig> configClass = annotationConfig.getClass();
		for (Field field : configClass.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			ConfigKey configKey = field.getAnnotation(ConfigKey.class);
			if (configKey == null
				|| Modifier.isTransient(modifiers)
				|| Modifier.isStatic(modifiers)) {
				continue;
			}
			// 获取路径，不存在则默认为根据变量名生成
			String path = configKey.path();
			if ("".equals(path)) {
				path = getTomlKey(field.getName());
			} else if (path.endsWith(".")){ // 以点结尾则根据变量名生成该项的名称
				path += getTomlKey(field.getName());
			}
			if (!pathSet.add(path)) { // 查重
				logger.warn("Duplicated path \"{}\" at {}#{}, ignored!", path, configClass.getName(), field.getName());
				continue;
			}
			// 获取其他注释
			Comment[] comments = configKey.comments();
			Map<String, String> commentsMap;
			if (comments.length > 0) {
				commentsMap = Arrays.stream(comments)
					.filter(comment -> {
						String path1 = comment.path();
						if (commentPathSet.add(path1)) {
							return true;
						}
						logger.warn("Duplicated comment path \"{}\" at {}#{}, ignored!", path1, configClass.getName(), field.getName());
						return false;
					})
					.collect(Collectors.toUnmodifiableMap(Comment::path, Comment::comment));
			} else {
				commentsMap = Map.of();
			}
			list.add(new ConfigFieldRecord(new FieldAccessor(annotationConfig, field), path, configKey.comment(), commentsMap));
		}
		return List.copyOf(list);
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
