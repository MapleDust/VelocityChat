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
	private @NotNull String globalChatPrefix = "#";
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
		Command tp can switch servers.""")
	private boolean commandTeleportSwitchServers = false;
	@Getter
	@ConfigKey(comment = """
		是否打印玩家命令日志
		Log player commands.""")
	private boolean logPlayerCommand = true;
	@Getter
	@ConfigKey(comment = """
		是否打印玩家命令日志
		Log player commands.""")
	private boolean logTells = true;
	@Getter
	@ConfigKey(comment = """
		是否打印子服务器聊天内容
		Log local chats.""")
	private boolean logLocalChats = false;
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
		是否可以使用“&”作为聊天格式化代码
		Enable color code in chat.""")
	private boolean colorableChat = true;
	@ConfigKey(comment = """
		设置命令别名，默认配置下：
		/br：切换到全局聊天
		/lc：切换到本地聊天
		注：仅玩家可用
		修改并重载后玩家需要重新加入游戏才会生效
		Set aliases.
		note: Players only.""")
	private @NotNull Config commandAlias = Config.wrap(Map.of(
		BROADCAST, BROADCAST_DEFAULT_ALIAS,
		LOCAL, LOCAL_DEFAULT_ALIAS,
		GLOBAL, GLOBAL_DEFAULT_ALIAS
	), Config.inMemory().configFormat());
	@Getter
	private @NotNull String commandGlobalAlias = GLOBAL_DEFAULT_ALIAS;
	@Getter
	private @NotNull String commandLocalAlias = LOCAL_DEFAULT_ALIAS;
	@Getter
	private @NotNull String commandBroadcastAlias = BROADCAST_DEFAULT_ALIAS;
	@Getter
	private @NotNull MessageChannel defaultMessageChannel = MessageChannel.GLOBAL;

	// TODO 文件缺失时不再写入内存中的内容，而是重新填入默认内容！

	private VelocityChatConfig(@NotNull Path configPath) {
		super(configPath);
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	public synchronized void load() {
		super.load();
		boolean shouldSave = false;
		Config commandAlias = this.commandAlias;
		String globalAlias = commandAlias.get(GLOBAL);
		if (globalAlias == null) {
			commandAlias.set(GLOBAL, GLOBAL_DEFAULT_ALIAS);
			globalAlias = GLOBAL_DEFAULT_ALIAS;
			shouldSave = true;
		}
		String localAlias = commandAlias.get(LOCAL);
		if (localAlias == null) {
			commandAlias.set(LOCAL, LOCAL_DEFAULT_ALIAS);
			localAlias = LOCAL_DEFAULT_ALIAS;
			shouldSave = true;
		}
		String broadcastAlias = commandAlias.get(BROADCAST);
		if (broadcastAlias == null) {
			commandAlias.set(BROADCAST, BROADCAST_DEFAULT_ALIAS);
			broadcastAlias = BROADCAST_DEFAULT_ALIAS;
			shouldSave = true;
		}
		if (shouldSave) {
			this.save();
		}
		commandGlobalAlias = globalAlias;
		commandLocalAlias = localAlias;
		commandBroadcastAlias = broadcastAlias;
		defaultMessageChannel = defaultGlobalChat ? MessageChannel.GLOBAL : MessageChannel.LOCAL;
	}
}
