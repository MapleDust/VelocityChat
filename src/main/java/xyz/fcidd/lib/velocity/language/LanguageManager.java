package xyz.fcidd.lib.velocity.language;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;

@SuppressWarnings("unused")
public interface LanguageManager extends TranslationRegistry {
	default void loadAndRegister() {
		load();
		register();
	}

	/**
	 * 从文件加载或重载语言文件
	 */
	void load();

	/**
	 * 注册翻译器
	 */
	void register();

	/**
	 * 注销翻译器
	 */
	void unregister();

	/**
	 * 创建语言文件加载器，创建完成后需要手动加载语言文件
	 *
	 * @param plugin         插件的实例，用于确定jar包路径，务必准确填写！
	 * @param translationKey 翻译键，不能和已有的重复
	 * @param langsFolder    语言文件输出/读取文件夹
	 * @param langsPathInJar 默认语言文件资源路径
	 * @return LanguageLoader对象
	 * @see LanguageManager#loadAndRegister()
	 */
	@Contract("_, _, _, _ -> new")
	static @NotNull LanguageManager create(@NotNull Object plugin, Key translationKey, @NotNull Path langsFolder, @NotNull String langsPathInJar) {
		return new LanguageManagerImpl(plugin, translationKey, langsFolder, langsPathInJar, false);
	}

	/**
	 * 创建语言文件加载器，创建完成后需要手动加载语言文件
	 *
	 * @param plugin         插件的实例，用于确定jar包路径，务必准确填写！
	 * @param translationKey 翻译键，不能和已有的重复
	 * @param langsFolder    语言文件输出/读取文件夹
	 * @param langsPathInJar 默认语言文件资源路径
	 * @param forceUpdate    是否自动更新
	 * @return LanguageLoader对象
	 * @see LanguageManager#loadAndRegister()
	 */
	@Contract("_, _, _, _, _ -> new")
	static @NotNull LanguageManager create(@NotNull Object plugin, Key translationKey, @NotNull Path langsFolder, @NotNull String langsPathInJar, boolean forceUpdate) {
		return new LanguageManagerImpl(plugin, translationKey, langsFolder, langsPathInJar, forceUpdate);
	}

	Set<String> keys();
}
