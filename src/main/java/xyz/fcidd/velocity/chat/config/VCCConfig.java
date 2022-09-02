package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Data;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static xyz.fcidd.velocity.chat.util.ILogger.LOGGER;

@Data
public class VCCConfig implements Serializable {
	private static final String CONFIG_VERSION = "1.0.0";
	private transient final Toml defaultToml;
	private transient final File tomlFile;
	@Setter
	private transient Toml toml;
	private String version;
	private String main_prefix;
	private Toml sub_prefix;
	private List<String> mcdr_command_prefix;
	private String chat_format;

	VCCConfig(Toml config, Toml defaultConfig, File tomlFile) {
		this.toml = config;
		this.defaultToml = defaultConfig;
		this.tomlFile = tomlFile;
		loadConfig();
		if (update()) save();
	}

	public void loadConfig(Toml toml) {
		this.toml = toml;
		loadConfig();
	}

	/**
	 * 加载/重载配置文件
	 */
	public void loadConfig() {
		version = toml.getString("version");
		main_prefix = toml.getString("main_prefix");
		sub_prefix = toml.getTable("sub_prefix");
		mcdr_command_prefix = toml.getList("mcdr_command_prefix");
		chat_format = toml.getString("chat_format");
	}

	/**
	 * 升级配置文件版本
	 *
	 * @return 升级成功 true 否则 false
	 */
	private boolean update() {
		if (!CONFIG_VERSION.equals(version)) {
			if (version == null) {
				chat_format = defaultToml.getString("chat_format");
			} else switch (version) {
				case "1.0.0" -> {
					// 升级逻辑
				}
			}
			version = CONFIG_VERSION;
			return true;
		}
		return false;
	}

	public void save() {
		TomlWriter writer = new TomlWriter();
		try {
			writer.write(this, tomlFile);
		} catch (IOException e) {
			LOGGER.error("配置文件保存失败！", e);
		}
	}
}
