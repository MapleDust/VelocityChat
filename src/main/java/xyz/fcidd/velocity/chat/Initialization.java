package xyz.fcidd.velocity.chat;

import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.ConfigManager;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

import static xyz.fcidd.velocity.chat.config.ConfigManager.*;

public class Initialization {

	/**
	 * 初始化插件
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SneakyThrows
	static void init() {
		// 如果配置文件不存在
		if (!CONFIG_FILE.exists()) {
			// 创建配置文件夹
			CONFIG_FOLDER.mkdirs();
			CONFIG_FILE.createNewFile();
			try (InputStream configResource = Initialization.class.getClassLoader().getResourceAsStream("config.toml");
				 FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
				Objects.requireNonNull(configResource);
				byte[] data = new byte[1024];
				int len;
				while ((len = configResource.read(data)) != -1) fos.write(data, 0, len);
			}
		} else {
			ConfigManager.reload();
		}
	}
}
