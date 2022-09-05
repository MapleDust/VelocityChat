package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import lombok.Getter;
import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.annotation.Comment;
import xyz.fcidd.velocity.chat.config.annotation.TomlConfig;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@TomlConfig
@Comment("配置文件")
public class VelocityChatConfig extends AbstractTomlConfig {
	@Getter
	@Comment("暂时无卵用，但请务必不要修改它")
	private String version = "1.0.0";
	@Getter
	@Comment("在此处填写 MCDR 命令的前缀,支持多个MCDR命令前缀，如果没有使用 MCDR 开服请保持默认，如果使用 MCDR 开服请根据实际情况填写")
	private List<String> mcdrCommandPrefix = List.of("!!");
	@Getter
	@Comment("聊天格式，暂时没用")
	private String chatFormat = "${main_prefix}${sub_prefix}§r<${player_name}> ${chat_message}";
	@Getter
	@Comment("是否打印玩家命令日志")
	private boolean logPlayerCommand = true;
	@Getter
	@Comment("主前缀")
	private String mainPrefix = "§8[§6群组§8]";
	@Getter
	@Comment("子服前缀")
	private Toml subPrefix = new Toml().read("""
			[sub_prefix]
			lobby = "§8[§a大厅§8]"
			""");

	VelocityChatConfig(Toml config, Path tomlPath) {
		super(config, tomlPath);
		if (load()) save();
	}

	/**
	 * 根据输入的Toml重载配置文件
	 */
	public void reload(Toml config) {
		this.toml = Objects.requireNonNull(config);
		load();
	}

	/**
	 * 加载/重载配置文件
	 *
	 * @return 输入的 Toml 缺键时为true，否则为false
	 */
	@SneakyThrows
	public boolean load() {
		if (toml.isEmpty()) return true;

		boolean hasNull = false;
		String version = this.getString("version");
		if (version == null) hasNull = true;
		else this.version = version;

		String mainPrefix = this.getString("main_prefix");
		if (mainPrefix == null) hasNull = true;
		else this.mainPrefix = mainPrefix;

		Toml subPrefix = this.getTable("sub_prefix");
		if (subPrefix == null) hasNull = true;
		else this.subPrefix = subPrefix;

		List<String> mcdrCommandPrefix = this.getList("mcdr_command_prefix");
		if (mcdrCommandPrefix == null) hasNull = true;
		else this.mcdrCommandPrefix = mcdrCommandPrefix;

		String chatFormat = this.getString("chat_format");
		if (chatFormat == null) hasNull = true;
		else this.chatFormat = chatFormat;

		Boolean logPlayerCommand = this.getBoolean("log_player_command");
		if (logPlayerCommand == null) hasNull = true;
		else this.logPlayerCommand = logPlayerCommand;

		return hasNull;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VelocityChatConfig vccConfig)) return false;
		if (!super.equals(o)) return false;

		if (logPlayerCommand != vccConfig.logPlayerCommand) return false;
		if (!Objects.equals(version, vccConfig.version)) return false;
		if (!Objects.equals(mcdrCommandPrefix, vccConfig.mcdrCommandPrefix))
			return false;
		if (!Objects.equals(chatFormat, vccConfig.chatFormat)) return false;
		if (!Objects.equals(mainPrefix, vccConfig.mainPrefix)) return false;
		return Objects.equals(subPrefix, vccConfig.subPrefix);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (mcdrCommandPrefix != null ? mcdrCommandPrefix.hashCode() : 0);
		result = 31 * result + (chatFormat != null ? chatFormat.hashCode() : 0);
		result = 31 * result + (logPlayerCommand ? 1 : 0);
		result = 31 * result + (mainPrefix != null ? mainPrefix.hashCode() : 0);
		result = 31 * result + (subPrefix != null ? subPrefix.hashCode() : 0);
		return result;
	}
}
