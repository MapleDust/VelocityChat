package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.annotation.Comment;
import xyz.fcidd.velocity.chat.config.annotation.TomlConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static xyz.fcidd.velocity.chat.util.ILogger.LOGGER;

public class TomlConfigs {
	private static Method get;

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public static <T> T get(Toml toml, String key) {
		if (get == null) {
			get = Toml.class.getDeclaredMethod("get", String.class);
			get.setAccessible(true);
		}
		return (T) get.invoke(toml, key);
	}

	public static String getString(Toml toml, String key) {
		try {
			return toml.getString(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Boolean getBoolean(Toml toml, String key) {
		try {
			return toml.getBoolean(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <T> List<T> getList(Toml toml, String key) {
		try {
			return toml.getList(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Toml getTable(Toml toml, String key) {
		try {
			return toml.getTable(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static List<Toml> getTables(Toml toml, String key) {
		try {
			return toml.getTables(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored"})
	public static void save(Object tomlConfig, Path tomlPath) {
		// 获取实际类型
		Class<?> clazz = tomlConfig.getClass();
		// 如果不是 Toml 则直接返回
		if (!clazz.isAnnotationPresent(TomlConfig.class)) return;

		/* 初始化参数 */

		// 获取参数列表
		List<Field> fields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
		// 如果列表为空则直接返回
		if (fields.isEmpty()) return;
		// 参数排序，如果是基本类型或列表则上升，否则下降
		fields.sort(Comparator.comparingInt(f -> getPriority(f, tomlConfig)));

		/* 准备写入文件 */

		// 用于从对象读取 Toml 字符串
		TomlWriter tomlWriter = new TomlWriter();
		// 创建缓存文件
		File tmp = tomlPath
				.getParent()
				.resolve(tomlPath.getFileName() + ".tmp")
				.toFile();
		try {
			if (!tmp.exists()) tmp.createNewFile();
		} catch (IOException e) {
			LOGGER.error("配置文件保存失败！");
			return;
		}

		/* 写入文件 */

		try (FileWriter fw = new FileWriter(tmp)) {
			// 向文件写入注释
			writeComment(fw, clazz.getAnnotation(Comment.class));
			// 空一行
			fw.write("\n");
			// 迭代所有参数
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				// !static && !transient
				if (!Modifier.isStatic(modifiers)
						&& !field.isSynthetic() // 甭管它
						&& !Modifier.isTransient(modifiers)
				) {

					/* 写入注解 */

					// 如果有注解
					if (field.isAnnotationPresent(Comment.class)) {
						// 向文件写入注释
						writeComment(fw, field.getAnnotation(Comment.class));
					}

					/* 写入 Toml */

					// 读取当前对象
					Object o;
					try {
						o = field.get(tomlConfig);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					// 格式化 Toml 键
					String tomlKeyName = field.getName();
					LOGGER.debug(tomlKeyName);
					StringBuilder sb = new StringBuilder();
					for (char c : tomlKeyName.toCharArray()) {
						if (Character.isUpperCase(c)) {
							sb.append("_");
							sb.append(Character.toLowerCase(c));
						} else {
							sb.append(c);
						}
					}
					// 获取 Toml 字符串
					String str;
					if (o == null) {
						str = sb.append(" = \"null\"\n").toString();
					} else if (o instanceof Toml toml) {
						str = tomlWriter.write(Map.of(sb.toString(), toml.toMap()));
					} else {
						str = tomlWriter.write(Map.of(sb.toString(), o));
					}
					// 写入 Toml 字符串
					fw.write(str + "\n");
					LOGGER.debug(str);
				}
			}
		} catch (IOException e) {
			LOGGER.error("配置文件保存失败！", e);
			return;
		}
		// 替换原配置文件
		File target = tomlPath.toFile();
		if (target.exists()) target.delete();
		if (!tmp.renameTo(target)) {
			LOGGER.error("配置文件保存失败！");
		}
	}

	/**
	 * 获取优先级，越小越高
	 */
	private static int getPriority(Field field, Object obj) {
		// 开放访问权限
		field.setAccessible(true);
		Object o1;
		try {
			o1 = field.get(obj);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		// 如果是基本类型或列表则上升，否则下降
		if (o1 instanceof String
				|| o1 instanceof Number
				|| o1 instanceof Boolean
				|| o1 instanceof List) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 写入注释
	 */
	private static void writeComment(FileWriter fw, Comment comment) throws IOException {
		// 向文件写入注释
		for (String str : comment.value()) {
			if (str.contains("\n")) {
				str = "# " + str.replace("\n", "\n# ");
			} else {
				str = "# " + str;
			}
			fw.write(str + "\n");
			LOGGER.debug("# {}", str);
		}
	}
}
