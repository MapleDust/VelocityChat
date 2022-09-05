package xyz.fcidd.velocity.chat;

import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.ConfigManager;

public class Initializer {

	/**
	 * 初始化插件
	 */
	@SneakyThrows
	static void init() {
		// 读取配置文件
		ConfigManager.load();
	}
}
