package xyz.fcidd.velocity.chat.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class AnnotationConfigs {
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

	private static CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static GenericBuilder<CommentedConfig, CommentedFileConfig> defaultConfigBuilder(@NotNull Path path) {
		return CommentedFileConfig
			.builder(path)
//				.autosave() // 自动保存
			.concurrent() // 线程安全
			.onFileNotFound(((file, configFormat) -> {
				file.getParent().toFile().mkdirs(); // 创建父目录
				file.toFile().createNewFile(); // 创建文件
				configFormat.initEmptyFile(file); // 获取文件格式
				return false; // 阻断后续操作，因为文件为空
			}))
			.charset(StandardCharsets.UTF_8)
			.parsingMode(ParsingMode.MERGE)
			.writingMode(WritingMode.REPLACE);
	}

	static synchronized void save(@NotNull AbstractAnnotationConfig annotationConfig, @NotNull CommentedFileConfig fileConfig) {
		future = future.thenRun(() -> {
			forEachLegalFields(annotationConfig, ((field, path, comment, isStatic) -> {
				if (!isStatic) fileConfig.set(path, field.get());
			}));
			fileConfig.save();
		});
	}

	//		VelocityWhitelistConfig.setInsertionOrderPreserved(true);
	static synchronized void load(@NotNull AbstractAnnotationConfig annotationConfig, @NotNull CommentedFileConfig fileConfig) {
		// 清空
		fileConfig.clear();
		// 加载
		fileConfig.load();
		forEachLegalFields(annotationConfig, (field, path, comment, isStatic) -> {
			// 如果不是 static 则赋值，static 修饰的参数仅用来承载注释
			if (!isStatic) {
				// 设置值
				Object fileConfigValue = fileConfig.get(path);
				if (fileConfigValue == null) {
					// 为null则反客为主将内存中的写入文件
					Object value = field.get();
					if (value == null) throw new IllegalArgumentException("默认值不能为null！");
					fileConfig.set(path, value);
				} else try {
					field.set(fileConfigValue);
				} catch (ClassCastException e) {
					// 文件给出的类型不对则反客为主将内存中的写入文件
					Object value = field.get();
					if (value == null) throw new IllegalArgumentException("默认值不能为null！");
					fileConfig.set(path, value);
				}
			}
			// 设置注释
			if (!"".equals(comment)
					&& fileConfig.getComment(path) == null) {
				fileConfig.setComment(path, comment);
			}
		});
		fileConfig.save();
	}

	private static @NotNull String getTomlKey(@NotNull String fieldName) {
		String tomlPath = CONFIG_KEY_CACHE.get(fieldName);
		if (tomlPath == null) {
			StringBuilder sb = new StringBuilder();
			for (char c : fieldName.toCharArray()) {
				if (Character.isUpperCase(c)) {
					sb.append('_');
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append(c);
				}
			}
			tomlPath = sb.toString();
			CONFIG_KEY_CACHE.put(fieldName, tomlPath);
		}
		return tomlPath;
	}

	private static void forEachLegalFields(@NotNull AbstractAnnotationConfig annotationConfig, @NotNull Consumer legalFieldsConsumer) {
		for (Field field : annotationConfig.getClass().getDeclaredFields()) {
			int modifiers = field.getModifiers();
			// 检查是否static
			boolean isStatic = Modifier.isStatic(modifiers);
			// 获取原始访问权限
			boolean originalAccessible;
			if (isStatic) originalAccessible = field.canAccess(null);
			else originalAccessible = field.canAccess(annotationConfig);
			// 设为可访问
			field.setAccessible(true);
			if (!field.isAnnotationPresent(ConfigKey.class)
					|| Modifier.isTransient(modifiers)) {
				continue;
			}
			ConfigKey annotation = field.getAnnotation(ConfigKey.class);
			// 获取路径，不存在则默认为根据变量名生成
			String path = annotation.path();
			if ("".equals(path)) path = getTomlKey(field.getName());
			// 如果不是 static 则赋值，static 修饰的参数仅用来承载注释
			legalFieldsConsumer.accept(new FieldAccessor(annotationConfig, field), path, annotation.comment(), isStatic);
			// 还原访问权限
			field.setAccessible(originalAccessible);
		}
	}

	@FunctionalInterface
	interface Consumer {
		void accept(FieldAccessor field, String path, String comment, Boolean isStatic);
	}

	record FieldAccessor(Object parentObj, Field field) {
			FieldAccessor(Object parentObj, @NotNull Field field) {
				field.setAccessible(true);
				this.field = field;
				this.parentObj = parentObj;
			}

			public Object get() {
				try {
					return field.get(parentObj);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e); // 不可达
				}
			}

			public void set(Object value) {
				try {
					field.set(parentObj, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e); // 不可达
				}
			}
		}
}
