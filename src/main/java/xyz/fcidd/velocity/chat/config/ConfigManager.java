package xyz.fcidd.velocity.chat.config;

import java.nio.file.Path;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public class ConfigManager {
	// 配置文件目录
	public static final Path CONFIG_PATH = DATA_DIRECTORY.resolve("config.toml");
	private static VelocityChatConfig config;

	/**
	 * 加载/重载配置文件
	 */
	public static void load() {
		if (config == null) {
			config = new VelocityChatConfig(CONFIG_PATH);
		} else {
			config.load();
		}
	}

	/**
	 * 获取配置文件
	 */
	public static VelocityChatConfig getConfig() {
		if (config == null) load();
		return config;
	}
}
