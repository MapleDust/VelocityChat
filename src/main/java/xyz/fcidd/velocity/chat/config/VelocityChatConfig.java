package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import lombok.Getter;
import lombok.SneakyThrows;
import xyz.fcidd.velocity.chat.config.annotation.ConfigKey;
import xyz.fcidd.velocity.chat.config.annotation.ConfigObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ConfigObject
@SuppressWarnings("FieldMayBeFinal")
public class VelocityChatConfig extends AbstractVelocityChatConfig {
	private static final String LATEST_VERSION = "1.1.0";

	@ConfigKey(comment = "配置文件版本，请务必不要修改它")
	private String version = "1.0.0";
	@Getter
	@ConfigKey(comment = """
			在此处填写 MCDR 命令的前缀，支持多个MCDR命令前缀
			如果没有使用 MCDR 开服请保持默认
			如果使用 MCDR 开服请根据实际情况填写""")
	private List<String> mcdrCommandPrefix = List.of("!!");
	@Getter
	@ConfigKey(comment = "是否打印玩家命令日志")
	private boolean logPlayerCommand = true;
	@Getter
	@ConfigKey(comment = """
			聊天格式
			#${proxy_name}: 群组名称
			#${server_name}: 服务器名称
			#${player_name}: 玩家名
			#${chat_message}: 聊天内容""")
	private String chatFormat = "${proxy_name}${server_name}§r<${player_name}§r> ${chat_message}";
	@Getter
	private String[] chatFormatArray;
	@Getter
	@ConfigKey(comment = "群组名称")
	private String proxyName = "§8[§6群组§8]";
	@Getter
	@ConfigKey(comment = "子服务器名称")
	private Config serverNames = CommentedConfig // 必须带注释
			.wrap(Map.of("lobby", lobby,
					"survival", survival), Config
					.inMemory()
					.configFormat());
	@ConfigKey(parent = "server_names", comment = "第一项为聊天中显示的名称，第二项为切换/进出服务器时显示的名称")
	private static final List<String> lobby = List.of("登录服", "大厅"); // 无效，仅用来承载默认注释
	@ConfigKey(parent = "server_names", comment = "仅有一项时两种场景共用名称")
	private static final List<String> survival = List.of("生存服"); // 无效，仅用来承载默认注释

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SneakyThrows
	public VelocityChatConfig(Path configPath) {
		super(CommentedFileConfig
				.builder(configPath)
				.autosave()
				.concurrent() // 线程安全
				.onFileNotFound(((file, configFormat) -> {
					file.getParent().toFile().mkdirs(); // 创建父目录
					file.toFile().createNewFile(); // 创建文件
					configFormat.initEmptyFile(file); // 获取文件格式
					return false; // 阻断后续操作，因为文件为空
				}))
				.charset(StandardCharsets.UTF_8)
				.parsingMode(ParsingMode.MERGE)
				.writingMode(WritingMode.REPLACE)
				.build());
		load();
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	@SneakyThrows
	public void load() {
		super.load();
		this.chatFormatArray = chatFormat.split("\\$(?=\\{)|(?<=\\$\\{[^}]+})");
		update();
	}

	private void update() {
		// 升级
		switch (this.version) {
			case LATEST_VERSION -> {
			}
			case "1.0.0" -> {
				Config serverNames = VelocityChatConfigs.getTable(config, "sub_prefix");
				if (serverNames != null) {
					// 恢复值
					this.serverNames = serverNames;
					config.set("server_names", serverNames);
					// 恢复注释
					String comment = config.getComment("sub_prefix");
					config.setComment("server_names",
							Objects.requireNonNullElse(comment, config.getComment("server_names")));
				}
				String proxyName = VelocityChatConfigs.getString(config, "main_prefix");
				if (proxyName != null) {
					// 恢复值
					this.proxyName = proxyName;
					config.set("proxy_name", proxyName);
					// 恢复注释
					String comment = config.getComment("main_prefix");
					config.setComment("proxy_name",
							Objects.requireNonNullElse(comment, config.getComment("proxy_name")));
				}
			}
			default -> throw new IllegalArgumentException("未识别的配置文件版本号！" + version);
		}
		this.version = LATEST_VERSION;
		config.set("version", LATEST_VERSION);
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
