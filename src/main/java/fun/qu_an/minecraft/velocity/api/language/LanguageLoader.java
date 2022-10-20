package fun.qu_an.minecraft.velocity.api.language;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface LanguageLoader extends TranslationRegistry {
	void loadOrReload();

	static @NotNull LanguageLoader create(@NotNull Object plugin, Key translationKey, @NotNull Path langsFolder, String langsPathInJar) {
		return new LanguageLoaderImpl(plugin, translationKey, langsFolder, langsPathInJar);
	}
}
