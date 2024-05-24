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
 * <p>配置项支持 基本类型、{@link String}、{@link List}、{@link Config}</p>
 * <p>若要将字段注册为配置项，对其添加{@link ConfigKey} 注解</p>
 * <p>该注解有三个可选的参数：</p>
 * <p>
 *
 * <p>path可以用于指定当前字段的配置项路径以及名称</p>
 * <p>本例的配置项名称将会设置为“my_setting”，原始字段名会被忽略</p>
 * <pre>{@code
 * @ConfigKey(path = "my_grandparent_path.my_parent_path.my_setting")
 * private String ignoredFieldName = "本项的默认值";}</pre>
 *
 * <p>以下是一个简单的字符串配置项示例：</p>
 * <p>当不填入参数时，配置项的名称由字段名决定，会在所有大写字母前加入“_”并改为小写</p>
 * <pre>{@code
 * @ConfigKey()
 * private String mySetting = "本项的默认值";}</pre>
 *
 * <p>路径以“.”结尾时，配置项的名称由字段名决定，本例的配置项名称将会设置为“my_setting”</p>
 * <pre>{@code
 * @ConfigKey(path = "my_grandparent_path.my_parent_path.")
 * private String mySetting = "本项的默认值";}</pre>
 * <p>
 *
 * <p>comment用于指定当前配置项的注释，使用文本块设置多行注释</p>
 * <pre>{@code
 * @ConfigKey(comment = """
 * 	这是一条配置文件注释
 * 	这是第二行注释""")
 * private String mySetting2 = "本项的默认值";}</pre>
 * <p>
 *
 * <p>comments用于指定任意路径配置项的注释，使用文本块设置多行注释</p>
 * <pre>{@code
 * @ConfigKey(
 *    comments = @Comment(
 * 		path = "path.to.the.comment",
 * 		comment = """
 * 		这是一条配置文件注释
 * 		这是第二行注释""")
 * private String mySetting2 = "本项的默认值";}</pre>
 * 或
 * <pre>{@code
 * @ConfigKey(
 *    comments = {@Comment(
 *        path = "path.to.the.comment",
 * 		comment = """
 * 		这是一条配置文件注释
 * 		这是第二行注释"""), @Comment(
 * 		path = "path.to.another.comment",
 * 		comment = """
 * 		这是一条配置文件注释
 * 		这是第二行注释""")}
 * private String mySetting2 = "本项的默认值";}</pre>
 */
// TODO 重构这一坨屎山
public abstract class AnnotationConfig {
	/**
	 * 使用 night-config 的带注释的配置文件
	 */
	private final CommentedFileConfig fileConfig;
	private Config defaultConfig = null;
	private volatile List<ConfigFieldRecord> fieldCache;
	private final boolean forceComments;

	private List<ConfigFieldRecord> getFieldCache() {
		if (fieldCache == null) {
			synchronized (fileConfig) {
				if (fieldCache == null) {
					return fieldCache = AnnotationConfigUtils.getConfigFields(this);
				}
			}
		}
		return fieldCache;
	}

	/**
	 * 使用默认配置设定读取目标文件
	 *
	 * @param path 目标文件路径
	 */
	protected AnnotationConfig(@NotNull Path path) {
		this(path, false);
	}

	/**
	 * 使用自定义的配置文件设定
	 *
	 * @param fileConfig 自定义的配置文件
	 */
	protected AnnotationConfig(CommentedFileConfig fileConfig) {
		this(fileConfig, false);
	}

	/**
	 * 使用默认配置设定读取目标文件
	 *
	 * @param path          目标文件路径
	 * @param forceComments 是否强制更新注释
	 */
	protected AnnotationConfig(@NotNull Path path, boolean forceComments) {
		this.fileConfig = AnnotationConfigUtils.defaultConfigBuilder(path).build();
		this.forceComments = forceComments;
	}

	/**
	 * 使用自定义的配置文件设定
	 *
	 * @param fileConfig    自定义的配置文件
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
			List<ConfigFieldRecord> fieldCache = getFieldCache();
			Config defaultConfig = this.defaultConfig;
			if (defaultConfig == null) {
				defaultConfig = this.defaultConfig = Config.inMemory();
				final Config finalDefaultConfig = defaultConfig;
				fieldCache.forEach(entry -> loadDefault(entry, finalDefaultConfig));
			}
			CommentedFileConfig fileConfig = this.fileConfig;
			fileConfig.clear(); // 先清空
			fileConfig.load();
			final Config finalDefaultConfig = defaultConfig;
			// 去除冗余项
			fileConfig.valueMap().keySet().stream()
				.filter(path -> !finalDefaultConfig.contains(path))
				.forEach(fileConfig::remove);
			fieldCache.forEach(entry -> load0(entry, fileConfig, finalDefaultConfig));
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
			getFieldCache().forEach(entry -> save0(entry, fileConfig));
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

	private void loadDefault(@NotNull ConfigFieldRecord entry, Config defaultConfig) {
		FieldAccessor field = entry.accessor();
		String path = entry.path();
		// 如果不是 static 则赋值，static 修饰的参数仅用来承载注释
//		if (!field.isStatic()) { // 没有 static 了
		// 设置值
		Object value = field.get();
		if (value == null) value = "null";
		defaultConfig.set(path, value);
//		}
	}

	private void load0(@NotNull ConfigFieldRecord entry, CommentedFileConfig fileConfig, Config defaultConfig) {
		FieldAccessor field = entry.accessor();
		String path = entry.path();
		// 如果不是 static 则赋值，static 修饰的参数仅用来承载注释
//		if (!field.isStatic()) { // 没有 static 了
		// 设置值
		Object fileConfigValue = fileConfig.get(path);
		if (fileConfigValue == null) {
			// 为null则将默认配置写入文件
			Object value = defaultConfig.get(path);
			if (value == null) value = "null";
			fileConfig.set(path, value);
		} else {
			try {
				field.set(fileConfigValue);
			} catch (ClassCastException e) {
				// 文件给出的类型不对则将默认配置写入文件
				Object value = defaultConfig.get(path);
				if (value == null) value = "null";
				fileConfig.set(path, value);
			}
		}
//		}
		// 设置注释
		String comment = entry.comment();
		if (!"".equals(comment) && (forceComments || fileConfig.getComment(path) == null)) {
			fileConfig.setComment(path, comment);
		}
		// 其他注释
		setOtherComments(fileConfig, path, entry.otherComments());
	}

	private void save0(@NotNull ConfigFieldRecord entry, CommentedFileConfig fileConfig) {
		FieldAccessor field = entry.accessor();
		String path = entry.path();
//		if (!field.isStatic()) { // 没有 static 了
		// 设置值
		fileConfig.set(path, field.get());
//		}
		// 设置注释
		String comment = entry.comment();
		if (!"".equals(comment) && (forceComments || !fileConfig.containsComment(path))) {
			fileConfig.setComment(path, comment);
		}
		// 其他注释
		setOtherComments(fileConfig, path, entry.otherComments());
	}

	private void setOtherComments(CommentedFileConfig fileConfig, String path, Map<String, String> otherComments) {
		if (otherComments.isEmpty()) {
			return;
		}
		if (forceComments) {
			otherComments.forEach(fileConfig::setComment);
			return;
		}
		otherComments.forEach((path1, comment1) -> {
			if (!fileConfig.containsComment(path)) {
				fileConfig.setComment(path1, comment1);
			}
		});
	}
}
