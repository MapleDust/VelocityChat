package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;

import java.nio.file.Path;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public final class ConfigManager {
	// 配置文件目录
	public static final Path CONFIG_PATH = DATA_DIRECTORY.resolve("config.toml");
	static {
		Config.setInsertionOrderPreserved(true); // 保留插入顺序
	}
	public static final VelocityChatConfig CONFIG = new VelocityChatConfig(CONFIG_PATH);

	/**
	 * 加载/重载配置文件
	 */
	public static void load() {
		CONFIG.load();
	}

}
