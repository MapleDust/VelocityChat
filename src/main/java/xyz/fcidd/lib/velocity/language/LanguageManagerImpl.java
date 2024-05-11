package xyz.fcidd.lib.velocity.language;

import lombok.SneakyThrows;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fcidd.lib.util.io.FileUtils;
import xyz.fcidd.lib.util.io.JarPathFormat;
import xyz.fcidd.lib.util.io.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static xyz.fcidd.lib.util.io.JarPathFormat.ENDS_WITH_SLASH;

final class LanguageManagerImpl implements LanguageManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(LanguageManager.class.getSimpleName());
	private final Class<?> pluginClass;
	private final Key registryKey;
	private final @NotNull Path langsFolder;
	private final @NotNull String langsPathInJar;
	private final boolean forceUpdate;
	private volatile TranslationRegistry registry;
	private final Set<String> keys = new HashSet<>();

	LanguageManagerImpl(@NotNull Object plugin, Key registryKey, @NotNull Path langsFolder, @NotNull String langsPathInJar, boolean forceUpdate) {
		this.pluginClass = plugin.getClass();
		this.registryKey = registryKey;
		this.langsFolder = langsFolder;
		this.langsPathInJar = JarPathFormat.format(langsPathInJar, ENDS_WITH_SLASH);
		this.forceUpdate = forceUpdate;
	}

	@Override
	public void load() {
		keys.clear(); // 清除缓存
		TranslationRegistry registry = TranslationRegistry.create(registryKey);
		// 已存在的语言文件
		Set<String> existLangFiles = new HashSet<>();
		FileUtils.forEachChild(langsFolder, file -> existLangFiles.add(file.getName()));
		// 如果该文件不存在则从jar中读取并复制到插件语言文件目录，存在则强制更新内容
		ResourceUtils.visitResourceFolder(pluginClass, langsPathInJar, (zipFile, zipEntry) -> visit(existLangFiles, zipFile, zipEntry));
		// 注册语言文件
		FileUtils.forEachChild(langsFolder, file -> {
			String localeName = file.getName()
				.replace(".properties", "")
				.replace('_', '-');
			Locale locale = Locale.forLanguageTag(localeName);
			registry.registerAll(locale, file.toPath(), false);
		});
		this.registry = registry;
	}

	@Override
	public void register() {
		GlobalTranslator.translator().addSource(this);
	}

	@Override
	public void unregister() {
		GlobalTranslator.translator().removeSource(this);
	}

	@Override
	public @Unmodifiable Set<String> keys() {
		return Set.copyOf(this.keys);
	}

	@SneakyThrows
	private void visit(@NotNull Set<String> existLangFiles, @NotNull ZipFile zipFile, @NotNull ZipEntry zipEntry) {
		String fileName = zipEntry.getName();
		fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
		LOGGER.debug("visiting {}", fileName);
		Path langsFolder = this.langsFolder;
		Path targetLangPath = langsFolder.resolve(fileName);
		// 如果不存在该语言文件则直接解压
		if (forceUpdate || !existLangFiles.contains(fileName)) {
			FileUtils.unpack(
				zipFile,
				zipEntry,
				targetLangPath.toFile());
			return;
		}
		LOGGER.debug("reading default lang");
		// 读取默认语言内容
		Properties defaultLang = new Properties();
		try (InputStream defaultIs = ResourceUtils.getAsStream(pluginClass, langsPathInJar + fileName)) {
			defaultLang.load(defaultIs);
		}
		File targetLangFile = targetLangPath.toFile();
		Path tmpPath = langsFolder.resolve(fileName + ".tmp");
		String registryKey = this.registryKey.asString();
		// 更新
		LOGGER.debug("Completing missing lang {}", registryKey);
		// 读取既存语言内容
		Properties existLang = new Properties();
		try (InputStream existIs = new FileInputStream(targetLangFile)) {
			existLang.load(existIs);
		}
		Set<String> keys = this.keys;
		existLang.keySet().forEach(k -> keys.add(k.toString())); // 缓存
		// 写入新键值到tmp
		BufferedWriter writer = null;
		try {
			for (Map.Entry<Object, Object> entry : defaultLang.entrySet()) {
				Object defaultKey = entry.getKey();
				Object defaultValue = entry.getValue();
				// 写入新键值
				if (!existLang.containsKey(defaultKey)) {
					String value = new String(defaultValue.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
						.replaceAll("\\n", "\\\\n\\\\\n");
					String key = new String(defaultKey.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
					keys.add(key); // 缓存
					if (writer == null) { // 懒加载
						// 复制
						File tmpFile = tmpPath.toFile();
						if (!FileUtils.copyWithoutBuffer(targetLangFile, tmpFile)) {
							throw new IOException("Copy failed");
						}
						writer = new BufferedWriter(new FileWriter(tmpFile, true));
					}
					writer.append('\n')
						.append(key)
						.append('=')
						.append(value)
						.write('\n');
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		if (writer == null) {
			return; // 无 tmp
		}
		// 将tmp重命名回原始文件名
		if (!targetLangFile.delete() || !tmpPath.toFile().renameTo(targetLangFile)) {
			LOGGER.error("Lang {} file {} update failed!", registryKey, fileName);
		}
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
		keys.add(key);
		registry.register(key, locale, format);
	}

	@Override
	public void unregister(@NotNull String key) {
		keys.remove(key);
		registry.unregister(key);
	}
}
