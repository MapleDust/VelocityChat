package fun.qu_an.lib.velocity.api.language;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface LanguageLoader extends TranslationRegistry {
	void load();

	static @NotNull LanguageLoader create(Path langsFolder, String langsFolderInJar) {
		return new LanguageLoaderImpl(langsFolder, langsFolderInJar);
	}
}
