package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import xyz.fcidd.velocity.chat.Initialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static xyz.fcidd.velocity.chat.util.ILogger.LOGGER;

public class ConfigManager {
	// 配置文件目录
	public static final File CONFIG_FILE = new File("./plugins/velocitychat/config.toml");
	// 配置文件夹
	public static final File CONFIG_FOLDER = new File("./plugins/velocitychat/");
	private static Toml toml;
	private static VCCConfig config;

	/**
	 * 重载配置文件
	 */
	public static void reload() {
		reloadToml();
		config.loadConfig(toml);
	}

	private static void reloadToml() {
		toml = new Toml().read(CONFIG_FILE);
	}

	/**
	 * 获取配置文件容器
	 */
	public static VCCConfig getConfig() {
		if (config == null) {
			if (toml == null) reloadToml();
			return config = new VCCConfig(toml, defaultToml(), CONFIG_FILE);
		}
		return config;
	}

	private static Toml defaultToml() {
		try (InputStream configResource = Initialization.class.getClassLoader().getResourceAsStream("config.toml")) {
			return new Toml().read(configResource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 保存配置文件
	 */
	public static void save() {
		config.save();
	}
}
