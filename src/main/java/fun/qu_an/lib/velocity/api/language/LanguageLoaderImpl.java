package fun.qu_an.lib.velocity.api.language;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import fun.qu_an.lib.basic.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public final class LanguageLoaderImpl implements LanguageLoader {
	private final String langsFolderInJar;
	private TranslationRegistry registry;
	private final Path langsFolder;

	LanguageLoaderImpl(Path langsFolder, String langsFolderInJar) {
		this.langsFolder = langsFolder;
		this.langsFolderInJar = langsFolderInJar;
	}

	@Override
	public void load() {
		this.registry = TranslationRegistry.create(Key.key("qu_an", "whitelist"));
		// 已存在的语言文件
		List<String> existLangFiles = new ArrayList<>();
		FileUtils.forEachChild(langsFolder, file -> existLangFiles.add(file.getName()));

		// 如果该文件不存在则从jar中读取并复制到插件语言文件目录
		FileUtils.visitResourceFolder(LanguageLoaderImpl.class, langsFolderInJar, (zipFile, zipEntry) -> {
			String zipEntryName = zipEntry.getName();
			if (!existLangFiles.contains(zipEntryName)) try {
				FileUtils.unpackWithoutBuffer(
					zipFile,
					zipEntry,
					DATA_DIRECTORY.resolve(zipEntryName).toFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		// 注册语言文件
		FileUtils.forEachChild(langsFolder, file -> {
			String localeName = file.getName()
				.replace(".properties", "")
				.replace('_', '-');
			Locale locale = Locale.forLanguageTag(localeName);
			registry.registerAll(locale, file.toPath(), false);
		});
		GlobalTranslator.get().addSource(registry);
	}

	@Override
	public @NotNull Key name() {
		return registry.name();
	}

	@Override
	public boolean contains(@NotNull String key) {
		return registry.contains(key);
	}

	@Override
	public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
		return registry.translate(key, locale);
	}

	@Override
	public void defaultLocale(@NotNull Locale locale) {
		registry.defaultLocale(locale);
	}

	@Override
	public void register(@NotNull String key, @NotNull Locale locale, @NotNull MessageFormat format) {
		registry.register(key, locale, format);
	}

	@Override
	public void unregister(@NotNull String key) {
		registry.unregister(key);
	}
}
