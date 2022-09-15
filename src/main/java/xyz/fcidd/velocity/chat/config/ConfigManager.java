package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;

import java.nio.file.Path;
import java.util.List;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

public final class ConfigManager {
	// 配置文件目录
	public static final Path CONFIG_PATH = DATA_DIRECTORY.resolve("config.toml");
	private static VelocityChatConfig config;

	/**
	 * 加载/重载配置文件
	 */
	public static void load() {
		if (config == null) {
			Config.setInsertionOrderPreserved(true); // 保留插入顺序
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

	public static String getServerSystemName(String serverId) {
		String ServerName;
		List<String> list = VcConfigs.getList(getConfig().getServerNames(), serverId);
		if (list == null || list.isEmpty()) {
			ServerName = serverId;
		} else if (list.size() == 1) {
			ServerName = list.get(0);
		} else {
			ServerName = list.get(1);
		}
		return ServerName;
	}

	public static String getServerChatName(String serverId) {
		// 获取服务器名称
		String serverName;
		// 获取子服的前缀
		List<String> list = VcConfigs.getList(config.getServerNames(), serverId);
		if (list == null || list.isEmpty()) {
			serverName = "§8[§r" + serverId + "§8]";
		} else {
			serverName = list.get(0); // 使用第一个，聊天中显示的名称
		}
		return serverName;
	}
}
