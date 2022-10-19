package xyz.fcidd.velocity.chat.translate;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import xyz.fcidd.velocity.chat.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public class LanguageLoader {
	private static final Path LANGS_FOLDER_PATH = DATA_DIRECTORY.resolve("langs");

	/**
	 * 加载语言文件，应在插件加载和重载时调用
	 */
	public static void load() {
		// 创建注册表
		TranslationRegistry registry = TranslationRegistry.create(Key.key("qu_an", "whitelist"));

		// 已存在的语言文件
		List<String> existLangFiles = new ArrayList<>();
		FileUtils.forEachChild(LANGS_FOLDER_PATH, file -> existLangFiles.add(file.getName()));

		// 如果该文件不存在则从jar中读取并复制到插件语言文件目录
		FileUtils.visitResourceFolder(LanguageLoader.class, "langs", (zipFile, zipEntry) -> {
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
		FileUtils.forEachChild(LANGS_FOLDER_PATH, file -> {
			String localeName = file.getName()
				.replace(".properties", "")
				.replace('_', '-');
			Locale locale = Locale.forLanguageTag(localeName);
			registry.registerAll(locale, file.toPath(), false);
		});
		GlobalTranslator.get().addSource(registry);
	}
}
