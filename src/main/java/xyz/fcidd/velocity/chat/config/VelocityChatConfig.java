package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.fcidd.lib.config.AnnotationConfig;
import xyz.fcidd.lib.config.ConfigKey;
import xyz.fcidd.velocity.chat.message.MessageChannel;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;
import static xyz.fcidd.velocity.chat.command.Commands.*;

public class VelocityChatConfig extends AnnotationConfig {
	public static final VelocityChatConfig CONFIG = new VelocityChatConfig(DATA_DIRECTORY.resolve("config.toml"));
	@Getter
	@ConfigKey(comment = """
		在此处填写 MCDR 命令的前缀，支持多个MCDR命令前缀
		如果没有使用 MCDR 开服请保持默认
		如果使用 MCDR 开服请根据实际情况填写，一般为“!!”
		全局聊天不会接管以列表中字符串为开头的聊天消息
		Send to current dedicated server only if the message starting with matched string.""")
	@NotNull
	private List<String> mcdrCommandPrefix = List.of();
	@Getter
	@ConfigKey(comment = """
		以该前缀开头的消息会自动发送到全局聊天
		Messages starting with the prefix will be sent to the global chat.""")
	private @NotNull String globalChatPrefix = "<";
	@Getter
	@ConfigKey(comment = """
		以该前缀开头的消息仅发送到子服务器聊天
		Messages starting with the prefix are only sent to local chat.""")
	private @NotNull String localChatPrefix = "\\";
	@Getter
	@ConfigKey(comment = """
		玩家每次加入游戏时是否默认开启全局聊天
		Enable default global chat.""")
	private boolean defaultGlobalChat = true;
	@Getter
	@ConfigKey(comment = """
		全局聊天时是否接管本地聊天
		Overwrite local chats when global chats.""")
	private boolean overwriteLocalChats = true;
	@Getter
	@ConfigKey(comment = """
		启用跨服TP（可能存在未知问题）
		Command tp can switch servers. (Unstable?)""")
	private boolean commandTeleportSwitchServers = false;
	@Getter
	@ConfigKey(comment = """
		是否打印玩家命令日志
		Log player commands.""")
	private boolean logPlayerCommands = false;
	@Getter
	@ConfigKey(comment = """
		是否打印玩家命令日志
		Log player commands.""")
	private boolean logPlayerTells = false;
	@Getter
	@ConfigKey(comment = """
		是否打印子服务器聊天内容
		Log local chats.""")
	private boolean logLocalChats = true;
	@Getter
	@ConfigKey(comment = """
		是否在ping时发送玩家列表（在客户端服务器列表显示玩家列表）
		Send sample players when client refreshing multiplayer games.""")
	private boolean sendPlayersOnPing = false;
	@Getter
	@ConfigKey(comment = """
		Tab列表是否显示全部群组玩家
		Show all proxy players on tab list.""")
	private boolean showGlobalTabList = false;
	@Getter
	@ConfigKey(comment = """
		是否对所有玩家启用“/glist”指令
		Enable command `glist`.""")
	private boolean enableCommandGlist = true;
	@Getter
	@ConfigKey(comment = """
		是否可以使用 MiniMessage 中的<_colorname_>、<_decorationname_>、<reset>格式作为聊天格式化代码
		Enable MiniMessage formats in chat. (<_colorname_>, <_decorationname_>, <reset>)
		MiniMessage (https://docs.advntr.dev/minimessage/format.html)""")
	private boolean formattableChat = true;
	@ConfigKey(comment = """
		设置命令别名
		修改并重载后玩家需要切换服务器或重新加入游戏才会生效
		Set aliases.""")
	private static @NotNull Config commandAlias = Config.wrap(Map.of(), Config.inMemory().configFormat());
	@Getter
	@ConfigKey(path = "command_alias." + GLOBAL)
	private @NotNull String commandGlobalAlias = GLOBAL_DEFAULT_ALIAS;
	@Getter
	@ConfigKey(path = "command_alias." + LOCAL)
	private @NotNull String commandLocalAlias = LOCAL_DEFAULT_ALIAS;
	@Getter
	@ConfigKey(path = "command_alias." + BROADCAST)
	private @NotNull String commandBroadcastAlias = BROADCAST_DEFAULT_ALIAS;
	@Getter
	@ConfigKey(path = "command_alias." + NOTIFY)
	private @NotNull String commandNotifyAlias = NOTIFY;
	@Getter
	@ConfigKey(path = "command_alias." + TELL)
	private @NotNull String commandTellAlias = TELL;
	@Getter
	@ConfigKey(path = "command_alias." + TELLCONSOLE)
	private @NotNull String commandTellconsoleAlias = TELLCONSOLE;
	@Getter
	private @NotNull MessageChannel defaultMessageChannel = MessageChannel.GLOBAL;

	private VelocityChatConfig(@NotNull Path configPath) {
		super(configPath, true);
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	public synchronized void load() {
		super.load();
		defaultMessageChannel = defaultGlobalChat ? MessageChannel.GLOBAL : MessageChannel.LOCAL;
	}
}
