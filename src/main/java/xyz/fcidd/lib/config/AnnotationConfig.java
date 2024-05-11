package xyz.fcidd.lib.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.lib.util.reflect.FieldAccessor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * <p>基于注解的配置文件</p>
 * <p>继承该类可以创建自定义的基于注解的配置文件类</p>
 * <p>使用了 <a href="https://github.com/TheElectronWill/night-config">night-config</a> 实现，支持 json yaml toml hocon 格式</p>
 * <p>
 *
 * <p>若要将字段注册为配置项，应对其添加{@link ConfigKey} 注解</p>
 * <p>该注解有两个可选的参数：</p>
 *
 * <p>path可以用于指定当前字段的配置项路径以及名称</p>
 * <p>本例的配置项名称将会设置为“my_setting”，原始字段名会被忽略</p>
 * <pre>{@code
 * @ConfigKey(path = "my_grandparent_path.my_parent_path.my_setting")
 * private String ignoredFieldName = "本项的默认值";}</pre>
 *
 * <p>以下是一个简单的字符串配置项示例：</p>
 * <p>当不填入参数时，配置项的名称由字段名决定，会在所有大写字母前加入“_”并改为小写</p>
 *
 * <pre>{@code
 * @ConfigKey()
 * private String mySetting = "本项的默认值";}</pre>
 * <p>
 *
 * <p>comment用于指定当前配置项的注释，支持使用文本块设置多行注释</p>
 * <pre>{@code
 * @ConfigKey(comment = """
 * 	这是一条配置文件注释
 * 	这是第二行注释""")
 * private String mySetting2 = "本项的默认值";}</pre>
 * <p>
 *
 * <p>配置文件中映射表类型的默认值必须使用任意的 {@link Config}</p>
 * <p>使用 {@link AnnotationConfigUtils#wrap} 方法可以将 {@link Map} 解析为 {@link Config}</p>
 *
 * <pre>{@code
 * @ConfigKey()
 * private CommentedConfig myMap = AnnotationConfigUtils.wrap(
 * 	Map.of(
 * 		"k1", ""
 * 		"k2", Map.of(
 * 			...
 * 		),
 * 		...
 * 	));}</pre>
 * <p>可以在初始化时为默认映射表中项目设置默认注释：</p>
 * <pre>{@code
 * public YourConfig(...) {
 * 		myMap.setComment(k2, "注释");
 * }}</pre>
 * <p>
 *
 * <p>static 修饰的参数仅用来承载注释，不会写入文件</p>
 * <pre>{@code
 * private static final CommentedConfig myMap = AnnotationConfigUtils.wrap(...));
 * }</pre>
 * <p>可以在静态块内为默认映射表中项目设置默认注释：</p>
 * <pre>{@code
 * static {
 * 		myMap.setComment(k2, "注释");
 * }}</pre>
 */
@SuppressWarnings("unused")
public abstract class AnnotationConfig {
	/**
	 * 使用 night-config 的带注释的配置文件
	 */
	protected final CommentedFileConfig fileConfig;
	private List<ConfigFieldRecord> fieldCache;
	private final boolean forceComments;

	/**
	 * 使用默认配置设定读取目标文件
	 *
	 * @param path               目标文件路径
	 */
	protected AnnotationConfig(@NotNull Path path) {
		this(path, false);
	}

	/**
	 * 使用自定义的配置文件设定
	 *
	 * @param fileConfig         自定义的配置文件
	 */
	protected AnnotationConfig(CommentedFileConfig fileConfig) {
		this(fileConfig, false);
	}

	/**
	 * 使用默认配置设定读取目标文件
	 *
	 * @param path               目标文件路径
	 * @param forceComments 是否强制更新注释
	 */
	protected AnnotationConfig(@NotNull Path path, boolean forceComments) {
		this.fileConfig = AnnotationConfigUtils.defaultConfigBuilder(path).build();
		this.forceComments = forceComments;
	}

	/**
	 * 使用自定义的配置文件设定
	 *
	 * @param fileConfig         自定义的配置文件
	 * @param forceComments 是否强制更新注释
	 */
	protected AnnotationConfig(CommentedFileConfig fileConfig, boolean forceComments) {
		this.fileConfig = fileConfig;
		this.forceComments = forceComments;
	}

	/**
	 * 加载配置文件，底层使用反射根据注解筛选实现类的成员变量并赋值
	 */
	protected void load() {
		synchronized (fileConfig) {
			CommentedFileConfig fileConfig = this.fileConfig;
			fileConfig.clear();
			fileConfig.load();
			if (fieldCache == null) {
				fieldCache = AnnotationConfigUtils.getConfigFields(this);
			}
			fieldCache.forEach(this::load0);
			fileConfig.save();
		}
	}

	/**
	 * 保存
	 */
	protected void save() {
		synchronized (fileConfig) {
			CommentedFileConfig fileConfig = this.fileConfig;
			fileConfig.clear();
			if (fieldCache == null) {
				fieldCache = AnnotationConfigUtils.getConfigFields(this);
			}
			fieldCache.forEach(this::save0);
			fileConfig.save();
		}
	}

	/**
	 * 保存（异步）
	 *
	 * @return CompletableFuture
	 */
	protected @NotNull CompletableFuture<Void> saveAsync() {
		return CompletableFuture.runAsync(this::save);
	}

	private void load0(@NotNull ConfigFieldRecord entry) {
		FieldAccessor field = entry.accessor();
		String path = entry.path();
		CommentedFileConfig fileConfig = this.fileConfig;
		// 如果不是 static 则赋值，static 修饰的参数仅用来承载注释
		if (!field.isStatic()) {
			// 设置值
			Object fileConfigValue = fileConfig.get(path);
			if (fileConfigValue == null) {
				// 为null则将内存中的写入文件
				Object value = field.get();
				if (value == null) value = "null";
				fileConfig.set(path, value);
			} else {
				try {
					field.set(fileConfigValue);
				} catch (ClassCastException e) {
					// 文件给出的类型不对则将内存中的写入文件
					Object value = field.get();
					if (value == null) value = "null";
					fileConfig.set(path, value);
				}
			}
		}
		// 设置注释
		String comment = entry.comment();
		if (!comment.equals("")) {
			if (forceComments) {
				fileConfig.setComment(path, comment);
			} else if (fileConfig.getComment(path) == null) {
				fileConfig.setComment(path, comment);
			}
		}
	}

	private void save0(@NotNull ConfigFieldRecord entry) {
		FieldAccessor field = entry.accessor();
		String path = entry.path();
		CommentedFileConfig fileConfig = this.fileConfig;
		if (!field.isStatic()) {
			fileConfig.set(path, field.get());
		}
		String comment = entry.comment();
		if (!comment.equals("")) {
			fileConfig.setComment(path, comment);
		}
	}
}
