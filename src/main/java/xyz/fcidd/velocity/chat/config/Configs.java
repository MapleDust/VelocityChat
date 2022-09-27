package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.annotation.ConfigKey;
import xyz.fcidd.velocity.chat.config.annotation.ConfigObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xyz.fcidd.velocity.chat.util.PluginUtil.LOGGER;

public class Configs {
	private static final Map<String, String> CONFIG_KEY_CACHE = new HashMap<>();

	public static String getString(Config config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Boolean getBoolean(Config config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <T> List<T> getList(Config config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static CommentedConfig getTable(CommentedConfig config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}


	//		Config.setInsertionOrderPreserved(true);
	@SneakyThrows
	public static void load(Object vcConfigObject, CommentedFileConfig fileConfig) {
		Class<?> clazz = vcConfigObject.getClass();
		if (!clazz.isAnnotationPresent(ConfigObject.class)) {
			throw new IllegalArgumentException(vcConfigObject.getClass().getName() + " is not config object!");
		}
		// 清空
		fileConfig.clear();
		// 加载
		fileConfig.load();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			int modifiers = field.getModifiers();
			if (!field.isAnnotationPresent(ConfigKey.class)
					|| Modifier.isTransient(modifiers)) {
				continue;
			}

			String tomlPath = getTomlKey(field.getName());
			ConfigKey annotation = field.getAnnotation(ConfigKey.class);
			String parentPath = annotation.parent();
			if (!"".equals(parentPath)) {
				if (!fileConfig.contains(parentPath)) continue;
				tomlPath = parentPath + "." + tomlPath;
			}

			// 如果不是 static 则赋值，static 修饰的参数仅用来承载默认注释
			if (!Modifier.isStatic(modifiers)) {
				// 设置值
				Object fileConfigValue = fileConfig.get(tomlPath);
				if (fileConfigValue == null) {
					// 为null则反客为主将内存中的写入文件
					fileConfig.set(tomlPath, field.get(vcConfigObject));
				} else {
					try {
						field.set(vcConfigObject, fileConfigValue);
						LOGGER.debug("将{}设为{}", tomlPath, fileConfigValue);
					} catch (ClassCastException e) {
						LOGGER.debug("{}的类型错误", tomlPath);
						// 文件给出的类型不对则反客为主将内存中的写入文件
						fileConfig.set(tomlPath, field.get(vcConfigObject));
					}
				}
			}
			String comment = annotation.comment();
			if (!"".equals(comment)
					&& !fileConfig.isNull(tomlPath)
					&& fileConfig.getComment(tomlPath) == null) {
				fileConfig.setComment(tomlPath, comment);
			}
		}

	}

	private static String getTomlKey(String fieldName) {
		String tomlPath;
		if (CONFIG_KEY_CACHE.containsKey(fieldName)) {
			tomlPath = CONFIG_KEY_CACHE.get(fieldName);
		} else {
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
}
