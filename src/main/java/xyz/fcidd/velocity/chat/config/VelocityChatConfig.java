package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.fcidd.velocity.chat.api.config.AbstractAnnotationConfig;
import xyz.fcidd.velocity.chat.api.config.AnnotationConfigs;
import xyz.fcidd.velocity.chat.api.config.ConfigKey;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

@SuppressWarnings("FieldMayBeFinal")
public class VelocityChatConfig extends AbstractAnnotationConfig {
	public static final VelocityChatConfig CONFIG = new VelocityChatConfig(DATA_DIRECTORY.resolve("config.toml"));
	static final String LATEST_VERSION = "1.2.0";
	static final String DEFAULT_CHAT_FORMAT = "§8[§r${proxy_name}§8][§r${server_name}§8]§r<${player_name}§r> ${chat_message}";
	static final String CHAT_FORMAT_REGEX = "\\$(?=\\{)|(?<=\\$\\{[^}]+})"; // 能正常使用，但idea会报错，原因未知

	@ConfigKey(comment = "配置文件版本，请务必不要修改它")
	@NotNull
	String version = LATEST_VERSION;
	@Getter
	@ConfigKey(comment = """
		在此处填写 MCDR 命令的前缀，支持多个MCDR命令前缀
		如果没有使用 MCDR 开服请保持默认
		如果使用 MCDR 开服请根据实际情况填写，一般为“!!”""")
	@NotNull
	List<String> mcdrCommandPrefix = List.of();
	@Getter
	@ConfigKey(comment = "是否打印玩家命令日志")
	boolean logPlayerCommand = true;
	@Getter
	@ConfigKey(comment = "群组名称")
	@NotNull
	String proxyName = "§6群组";
	@Getter
	@ConfigKey()
	@NotNull
	CommentedConfig servers = CommentedConfig // 必须CommentedConfig
		.wrap(Map.of(
			"lobby", CommentedConfig.wrap(Map.of(
					"name", "登录服",
					"chat_format", DEFAULT_CHAT_FORMAT),
				Config.inMemory().configFormat()),
			"survival", CommentedConfig.wrap(Map.of(
					"name", "生存服"),
				Config.inMemory().configFormat())
		), Config.inMemory().configFormat());
	@ConfigKey(path = "servers.lobby.name", comment = "子服务器名称")
	static final @Nullable String name = null;
	@ConfigKey(path = "servers.lobby.chat_format", comment = "留空以使用默认")
	static final @Nullable String chatFormat = null;
	@Getter
	@ConfigKey(comment = "是否在ping时发送玩家列表")
	boolean sendPlayersOnPing = true;
	@ConfigKey(comment = """
		默认聊天格式
		${proxy_name}: 群组名称
		${server_name}: 服务器名称
		${player_name}: 玩家名
		${chat_message}: 聊天内容""")
	@NotNull
	String defaultChatFormat = DEFAULT_CHAT_FORMAT;
	@Getter
	@ConfigKey(comment = """
		Tab列表是否显示全部群组玩家
		** 暂不推荐使用，会影响指令提示 **""")
	boolean showGlobalTabList = false;
	@Getter
	Component proxyNameComponent;
	String[] defaultChatFormatArray;
	final Map<String, String[]> chatFormatArrays = new HashMap<>();

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public VelocityChatConfig(@NotNull Path configPath) {
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
		CommentedConfig table = AnnotationConfigs.getTable(servers, serverId);
		return table == null ? serverId : Objects.requireNonNull(AnnotationConfigs.getString(table, "name"));
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
		proxyNameComponent = Component.text(proxyName);
	}
}
