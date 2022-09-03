package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import lombok.Data;
import lombok.Setter;

import java.io.*;
import java.util.List;

@Data
public class VCCConfig implements Serializable {
	private transient final Toml defaultToml;
	private transient final String defaultVersion;
	private transient final File tomlFile;
	@Setter
	private transient Toml toml;
	private String version;
	private String mainPrefix;
	private Toml subPrefix;
	private List<String> mcdrCommandPrefix;
	private String chatFormat;
	private boolean logPlayerCommand;

	VCCConfig(Toml config, Toml defaultConfig, File tomlFile) {
		this.toml = config;
		this.defaultToml = defaultConfig;
		this.defaultVersion = defaultConfig.getString("version");
		this.tomlFile = tomlFile;
		loadConfig();
	}

	/**
	 * 根据输入的Toml重载配置文件
	 */
	public void reloadConfig(Toml toml) {
		this.toml = toml;
		loadConfig();
	}

	/**
	 * 加载/重载配置文件
	 */
	public void loadConfig() {
		version = toml.getString("version");
		mainPrefix = toml.getString("main_prefix");
		subPrefix = toml.getTable("sub_prefix");
		mcdrCommandPrefix = toml.getList("mcdr_command_prefix");
		chatFormat = toml.getString("chat_format");
		logPlayerCommand = toml.getBoolean("log_player_command");
	}
}
