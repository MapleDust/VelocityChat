package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public class ConfigManager {
	// 配置文件目录
	public static final Path CONFIG_PATH = DATA_DIRECTORY.resolve("config.toml");
	public static final File CONFIG_FILE = CONFIG_PATH.toFile();
	// 配置文件夹
	public static final File CONFIG_FOLDER = DATA_DIRECTORY.toFile();
	private static Toml toml;
	private static VelocityChatConfig config;

	/**
	 * 加载/重载配置文件
	 */
	public static void load() {
		if (config == null) {
			// 加载
			if (toml == null) readToml();
			config = new VelocityChatConfig(toml, CONFIG_PATH);
		} else {
			// 重载
			readToml();
			config.reload(toml);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static void readToml() {
		if (!CONFIG_FILE.exists()) {
			CONFIG_FOLDER.mkdirs();
			try {
				CONFIG_FILE.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			toml = new Toml();
			return;
		}
		toml = new Toml().read(CONFIG_FILE);
	}

	/**
	 * 获取配置文件
	 */
	public static VelocityChatConfig getConfig() {
		if (config == null) load();
		return config;
	}

	/**
	 * 异步保存
	 */
	public static void saveAsync() {
		CompletableFuture.runAsync(config::save);
	}

	/**
	 * 保存，非异步，仅当在退出时调用！
	 */
	public static void save() {
		config.save();
	}
}
