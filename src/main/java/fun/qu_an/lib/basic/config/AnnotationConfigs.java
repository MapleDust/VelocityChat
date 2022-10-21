package fun.qu_an.lib.basic.config;

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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class AnnotationConfigs {
	private static final Map<String, String> CONFIG_KEY_CACHE = new WeakHashMap<>();

	/**
	 * 从配置文件的指定路径获取字符串
	 *
	 * @param config 配置文件
	 * @param path   键路径
	 * @return 不存在或出现任意错误时返回 null ，否则返回获取的字符串
	 */
	public static @Nullable String getString(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 从配置文件的指定路径获取布尔值
	 *
	 * @param config 配置文件
	 * @param path   键路径
	 * @return 不存在或出现任意错误时返回 null ，否则返回获取的布尔值
	 */
	public static @Nullable Boolean getBoolean(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 从配置文件的指定路径获取列表
	 *
	 * @param config 配置文件
	 * @param path   键路径
	 * @return 不存在或出现任意错误时返回 null ，否则返回获取的列表
	 */
	public static <T> @Nullable List<T> getList(@NotNull Config config, String path) {
		try {
			return config.get(path);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 从配置文件的指定路径获取映射表
	 *
	 * @param config 配置文件
	 * @param path   键路径
	 * @return 不存在或出现任意错误时返回 null ，否则返回获取的映射表
	 */
	public static @Nullable <T extends Config> T getTable(@NotNull T config, String path) {
		try {
			return config.get(path);
		} catch (Exception e) {
			return null;
		}
	}

	private static final Executor SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

	/**
	 * 获取带有默认设置的配置文件构建器
	 *
	 * @param path 配置文件路径
	 * @return 带有默认设置的配置文件构建器
	 */
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

	/**
	 * 保存配置文件内容
	 *
	 * @param annotationConfig 带注解的配置文件实例
	 * @param fileConfig       内部使用的配置文件实例
	 */
	static void save(@NotNull AbstractAnnotationConfig annotationConfig, @NotNull final CommentedFileConfig fileConfig) {
		synchronized (fileConfig) {
			fileConfig.clear();
			forEachLegalFields(annotationConfig, ((field, path, comment, isStatic) -> {
				if (!isStatic) fileConfig.set(path, field.get());
			}));
			fileConfig.save();
		}
	}

	/**
	 * 加载配置文件内容
	 *
	 * @param annotationConfig 带注解的配置文件实例
	 * @param fileConfig       内部使用的配置文件实例
	 */
	//		VelocityWhitelistConfig.setInsertionOrderPreserved(true);
	static void load(@NotNull AbstractAnnotationConfig annotationConfig, @NotNull CommentedFileConfig fileConfig) {
		synchronized (fileConfig) {
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

	/**
	 * 字段访问器，字段无法访问时会抛出运行时异常
	 *
	 * @param parentObj 字段所在的实例，可为空
	 * @param field     字段
	 */
	record FieldAccessor(@Nullable Object parentObj, @NotNull Field field) {
		/**
		 * 获取字段的值，字段无法访问时会抛出运行时异常
		 *
		 * @return 字段的值
		 */
		public Object get() {
			try {
				return field.get(parentObj);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * 设置字段的值，字段无法访问时会抛出运行时异常
		 *
		 * @param value 字段的值
		 */
		public void set(Object value) {
			synchronized (this) {
				try {
					field.set(parentObj, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
