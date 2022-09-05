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
	private static final String latestVersion = "1.1.0";
	@Getter
	@Comment("请务必不要修改它")
	private String version = latestVersion;
	@Getter
	@Comment("在此处填写 MCDR 命令的前缀,支持多个MCDR命令前缀，如果没有使用 MCDR 开服请保持默认，如果使用 MCDR 开服请根据实际情况填写")
	private List<String> mcdrCommandPrefix = List.of("!!");
	@Getter
	@Comment("聊天格式")
	private String chatFormat = "§8[${proxy_name}§8][${server_name}§8]§r<${player_name}§r> ${chat_message}";
	@Getter
	private transient String[] chatFormatArray = splitChatFormat();
	@Getter
	@Comment("是否打印玩家命令日志")
	private boolean logPlayerCommand = true;
	@Getter
	@Comment("群组名称")
	private String proxyName = "§6群组";
	@Getter
	@Comment("子服务器名称")
	private Toml serverNames = new Toml().read("""
			[server_names]
			lobby = "§a大厅"
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
	 * @return 是否需要保存
	 */
	@SneakyThrows
	public boolean load() {
		if (toml.isEmpty()) return true;

		boolean shouldSave = false;

		String version = this.getString("version");
		if (version == null) shouldSave = true;
		else this.version = version;

		String proxyName = this.getString("proxy_name");
		if (proxyName == null) shouldSave = true;
		else this.proxyName = proxyName;

		Toml serverNames = this.getTable("server_names");
		if (serverNames == null) shouldSave = true;
		else this.serverNames = serverNames;

		List<String> mcdrCommandPrefix = this.getList("mcdr_command_prefix");
		if (mcdrCommandPrefix == null) shouldSave = true;
		else this.mcdrCommandPrefix = mcdrCommandPrefix;

		String chatFormat = this.getString("chat_format");
		if (chatFormat == null) shouldSave = true;
		else {
			this.chatFormat = chatFormat;
			this.chatFormatArray = splitChatFormat();
		}

		Boolean logPlayerCommand = this.getBoolean("log_player_command");
		if (logPlayerCommand == null) shouldSave = true;
		else this.logPlayerCommand = logPlayerCommand;

		// 升级
		switch (this.version) {
			case latestVersion -> {
				return shouldSave;
			}
			case "1.0.0" -> {
				serverNames = this.getTable("sub_prefix");
				if (serverNames != null) this.serverNames = serverNames;
				proxyName = this.getString("main_prefix");
				if (proxyName != null) this.proxyName = proxyName;
			}
			default -> throw new IllegalArgumentException("未识别的配置文件版本号！" + version);
		}
		this.version = latestVersion;
		return true;
	}

	private String[] splitChatFormat() {
		return chatFormat.split("\\$(?=\\{)|(?<=\\$\\{[^}]+})");
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
		if (!Objects.equals(proxyName, vccConfig.proxyName)) return false;
		return Objects.equals(serverNames, vccConfig.serverNames);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (mcdrCommandPrefix != null ? mcdrCommandPrefix.hashCode() : 0);
		result = 31 * result + (chatFormat != null ? chatFormat.hashCode() : 0);
		result = 31 * result + (logPlayerCommand ? 1 : 0);
		result = 31 * result + (proxyName != null ? proxyName.hashCode() : 0);
		result = 31 * result + (serverNames != null ? serverNames.hashCode() : 0);
		return result;
	}
}
