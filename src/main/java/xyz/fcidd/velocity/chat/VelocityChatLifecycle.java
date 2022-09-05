package xyz.fcidd.velocity.chat;

import xyz.fcidd.velocity.chat.config.ConfigManager;

public class VelocityChatLifecycle {

	/**
	 * 初始化插件
	 */
	static void init() {
		ConfigManager.load();
	}

	/**
	 * 重载插件
	 */
	public static void reload() {
		ConfigManager.load();
	}
}
