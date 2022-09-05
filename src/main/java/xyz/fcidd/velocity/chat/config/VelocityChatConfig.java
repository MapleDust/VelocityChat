package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VelocityChatConfig {
	private static final String LATEST_VERSION = "1.1.0";
	private static final String VERSION = "version";
	private static final String MCDR_COMMAND_PREFIX = "mcdr_command_prefix";
	private static final String CHAT_FORMAT = "chat_format";
	private static final String LOG_PLAYER_COMMAND = "log_player_command";
	private static final String PROXY_NAME = "proxy_name";
	private static final String SERVER_NAMES = "server_names";

	/* 在创建创建检查器之前设置保持插入顺序 */
	static {
		// 保持插入顺序
		Config.setInsertionOrderPreserved(true);
	}

	/* 创建检查器 */
	private static final ConfigSpec configSpec = new ConfigSpec();

	/* 检查器默认值 */
	static {
		configSpec.define(VERSION, "1.0.0");
		configSpec.define(PROXY_NAME, "§8[§6群组§8]");
		configSpec.define(CHAT_FORMAT, "${proxy_name}${server_name}§r<${player_name}§r> ${chat_message}");
		configSpec.define(LOG_PLAYER_COMMAND, true);
		configSpec.define(MCDR_COMMAND_PREFIX, List.of("!!"));
		configSpec.define(SERVER_NAMES, Config.wrap(Map.of("lobby", "§8[§a大厅§8]"), Config.inMemory().configFormat()));
	}

	private String version;
	@Getter
	private List<String> mcdrCommandPrefix;
	@Getter
	private String chatFormat;
	@Getter
	private String[] chatFormatArray;
	@Getter
	private boolean logPlayerCommand;
	@Getter
	private String proxyName;
	@Getter
	private Config serverNames;
	private final CommentedFileConfig config;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SneakyThrows
	VelocityChatConfig(Path configPath) {
		config = CommentedFileConfig
				.builder(configPath)
				.autosave()
				.concurrent() // 线程安全
				.preserveInsertionOrder() // 保留插入顺序，无卵用，因为已在静态块设置过了
				.onFileNotFound(((file, configFormat) -> {
					file.getParent().toFile().mkdirs();
					file.toFile().createNewFile();
					configFormat.initEmptyFile(file);
					return false;
				}))
				.charset(StandardCharsets.UTF_8)
				.parsingMode(ParsingMode.MERGE)
				.writingMode(WritingMode.REPLACE)
				.build();
		load();
	}

	/**
	 * 加载/重载配置文件
	 */
	@SneakyThrows
	public void load() {
		config.load();
		configSpec.correct(config);

		version = config.get(VERSION);
		proxyName = config.get(PROXY_NAME);
		chatFormat = config.get(CHAT_FORMAT);
		chatFormatArray = splitChatFormat(chatFormat);
		logPlayerCommand = config.get(LOG_PLAYER_COMMAND);
		mcdrCommandPrefix = config.get(MCDR_COMMAND_PREFIX);
		serverNames = config.get(SERVER_NAMES);

		// 如果没有注释则写入默认注释
		if (config.getComment(VERSION) == null) {
			config.setComment(VERSION,
					"请务必不要修改它");
		}
		if (config.getComment(PROXY_NAME) == null) {
			config.setComment(PROXY_NAME,
					"群组名称");
		}
		if (config.getComment(SERVER_NAMES) == null) {
			config.setComment(SERVER_NAMES,
					"子服务器名称");
		}
		if (config.getComment(LOG_PLAYER_COMMAND) == null) {
			config.setComment(LOG_PLAYER_COMMAND,
					"是否打印玩家命令日志");
		}
		if (config.getComment(MCDR_COMMAND_PREFIX) == null) {
			config.setComment(MCDR_COMMAND_PREFIX, """
					在此处填写 MCDR 命令的前缀，支持多个MCDR命令前缀
					如果没有使用 MCDR 开服请保持默认
					如果使用 MCDR 开服请根据实际情况填写""");
		}
		if (config.getComment(CHAT_FORMAT) == null) {
			config.setComment(CHAT_FORMAT, """
					聊天格式
					#${proxy_name}: 群组名称
					#${server_name}: 服务器名称
					#${player_name}: 玩家名
					#${chat_message}: 聊天内容""");
		}

		// 升级
		switch (this.version) {
			case LATEST_VERSION -> {
			}
			case "1.0.0" -> {
				Config serverNames = TomlConfigs.getTable(config, "sub_prefix");
				if (serverNames != null) {
					// 恢复值
					this.serverNames = serverNames;
					config.set(SERVER_NAMES, serverNames);
					// 恢复注释
					String comment = config.getComment("sub_prefix");
					config.setComment(SERVER_NAMES, Objects.requireNonNullElse(comment, "子服务器名称"));
				}
				String proxyName = TomlConfigs.getString(config, "main_prefix");
				if (proxyName != null) {
					// 恢复值
					this.proxyName = proxyName;
					config.set(PROXY_NAME, proxyName);
					// 恢复注释
					String comment = config.getComment("main_prefix");
					config.setComment(PROXY_NAME, Objects.requireNonNullElse(comment, "群组名称"));
				}
			}
			default -> throw new IllegalArgumentException("未识别的配置文件版本号！" + version);
		}
		this.version = LATEST_VERSION;
		config.set(VERSION, LATEST_VERSION);
	}

	private String[] splitChatFormat(String chatFormat) {
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
