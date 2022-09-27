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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ConfigObject
@SuppressWarnings("FieldMayBeFinal")
public class VelocityChatConfig extends AbstractConfig {
	private static final String LATEST_VERSION = "1.2.0";
	private static final String DEFAULT_CHAT_FORMAT = "§8[§r${proxy_name}§8][§r${server_name}§8]§r<${player_name}§r> ${chat_message}";
	private static final String CHAT_FORMAT_REGEX = "\\$(?=\\{)|(?<=\\$\\{[^}]+})";

	@ConfigKey(comment = "配置文件版本，请务必不要修改它")
	private String version = LATEST_VERSION;
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
	@ConfigKey(comment = "群组名称")
	private String proxyName = "§6群组";
	@Getter
	@ConfigKey()
	private CommentedConfig servers = CommentedConfig // 必须CommentedConfig
			.wrap(Map.of(
					"lobby", CommentedConfig.wrap(Map.of(
									"name", "登录服",
									"chat_format", DEFAULT_CHAT_FORMAT),
							Config.inMemory().configFormat()),
					"survival", CommentedConfig.wrap(Map.of(
									"name", "生存服"),
							Config.inMemory().configFormat())
			), Config.inMemory().configFormat());
	@ConfigKey(parent = "servers.lobby", comment = "子服务器名称")
	private static final String name = null;
	@ConfigKey(parent = "servers.lobby", comment = "留空以使用默认")
	private static final String chatFormat = null;
	@Getter
	@ConfigKey(comment = "是否在ping时发送玩家列表")
	private boolean sendPlayersOnPing = true;
	@ConfigKey(comment = """
			默认聊天格式
			${proxy_name}: 群组名称
			${server_name}: 服务器名称
			${player_name}: 玩家名
			${chat_message}: 聊天内容""")
	private String defaultChatFormat = DEFAULT_CHAT_FORMAT;
	private String[] defaultChatFormatArray;
	private final Map<String, String[]> chatFormatArrays = new HashMap<>();

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public VelocityChatConfig(Path configPath) {
		super(CommentedFileConfig
				.builder(configPath)
				.autosave() // 自动保存
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
	}

	public String[] getChatFormat(String serverId) {
		String[] chatFormat = chatFormatArrays.get(serverId);
		return chatFormat == null ? defaultChatFormatArray : chatFormat;
	}

	public String getServerName(String serverId) {
		CommentedConfig table = Configs.getTable(servers, serverId);
		return table == null ? serverId : Objects.requireNonNull(Configs.getString(table, "name"));
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	@SneakyThrows
	public void load() {
		super.load();
		servers.entrySet().forEach(entry -> {
			String chatFormat = ((CommentedConfig) entry
					.getValue())
					.get("chat_format");
			if (chatFormat != null) {
				chatFormatArrays.put(entry.getKey(),
						chatFormat.split(CHAT_FORMAT_REGEX));
			}
		});
		defaultChatFormatArray = defaultChatFormat.split(CHAT_FORMAT_REGEX);
		update();
	}

	private void update() {
		// 升级
		switch (this.version) {
			case LATEST_VERSION:
				break;
			case "1.0.0":
				to1_1_0();
			case "1.1.0":
				to1_2_0();
				break;
			default:
				throw new IllegalArgumentException("未识别的配置文件版本号！" + version);
		}
		this.version = LATEST_VERSION;
		config.set("version", LATEST_VERSION);
	}

	private void to1_2_0() {
		config.remove("server_name");
	}

	private void to1_1_0() {
		CommentedConfig servers = Configs.getTable(config, "sub_prefix");
		if (servers != null) {
			// 恢复值
			this.servers = servers;
			config.set("server_names", servers);
			// 恢复注释
			String comment = config.getComment("sub_prefix");
			config.setComment("server_names",
					Objects.requireNonNullElse(comment, config.getComment("server_names")));
		}
		String proxyName = Configs.getString(config, "main_prefix");
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VelocityChatConfig vccConfig)) return false;
		if (!super.equals(o)) return false;

		if (logPlayerCommand != vccConfig.logPlayerCommand) return false;
		if (!Objects.equals(version, vccConfig.version)) return false;
		if (!Objects.equals(mcdrCommandPrefix, vccConfig.mcdrCommandPrefix))
			return false;
		if (!Objects.equals(proxyName, vccConfig.proxyName)) return false;
		return Objects.equals(servers, vccConfig.servers);
	}

	@Override
	public int hashCode() {
		int result = version != null ? version.hashCode() : 0;
		result = 31 * result + (mcdrCommandPrefix != null ? mcdrCommandPrefix.hashCode() : 0);
		result = 31 * result + (logPlayerCommand ? 1 : 0);
		result = 31 * result + (proxyName != null ? proxyName.hashCode() : 0);
		result = 31 * result + (servers != null ? servers.hashCode() : 0);
		return result;
	}
}
