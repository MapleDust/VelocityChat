package fun.qu_an.lib.minecraft.velocity.language;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@SuppressWarnings("unused")
public interface LanguageLoader extends TranslationRegistry {
	void loadOrReload();

	/**
	 * 创建语言文件加载器，创建完成后需要手动加载语言文件
	 *
	 * @param plugin         插件的实例，用于确定jar包路径，务必准确填写！
	 * @param translationKey 翻译键，不能和已有的重复
	 * @param langsFolder    语言文件输出/读取文件夹
	 * @param langsPathInJar 默认语言文件资源路径
	 * @return LanguageLoader对象
	 */
	@Contract("_, _, _, _ -> new")
	static @NotNull LanguageLoader create(@NotNull Object plugin, Key translationKey, @NotNull Path langsFolder, @NotNull String langsPathInJar) {
		return new LanguageLoaderImpl(plugin, translationKey, langsFolder, langsPathInJar);
	}
}
