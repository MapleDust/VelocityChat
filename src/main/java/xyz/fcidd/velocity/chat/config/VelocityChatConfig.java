package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;
import fun.qu_an.lib.basic.config.AnnotationConfig;
import fun.qu_an.lib.basic.config.ConfigKey;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;
import static xyz.fcidd.velocity.chat.command.Commands.*;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public class VelocityChatConfig extends AnnotationConfig {
	public static final VelocityChatConfig CONFIG = new VelocityChatConfig(DATA_DIRECTORY.resolve("config.toml"));
	@Getter
	@ConfigKey(comment = """
		在此处填写 MCDR 命令的前缀，支持多个MCDR命令前缀
		如果没有使用 MCDR 开服请保持默认
		如果使用 MCDR 开服请根据实际情况填写，一般为“!!”
		全局聊天不会接管以列表中字符串为开头的聊天消息""")
	@NotNull
	List<String> mcdrCommandPrefix = List.of();
	@Getter
	@ConfigKey(comment = "是否开启默认全局聊天")
	private boolean defaultGlobalChat = true;
	@Getter
	@ConfigKey(comment = "是否打印玩家命令日志")
	private boolean logPlayerCommand = true;
	@Getter
	@ConfigKey(comment = "是否在ping时发送玩家列表（在客户端服务器列表显示玩家列表）")
	private boolean sendPlayersOnPing = false;
	@Getter
	@ConfigKey(comment = "Tab列表是否显示全部群组玩家")
	private boolean showGlobalTabList = false;
	@Getter
	@ConfigKey(comment = "是否对所有玩家启用“/glist”指令")
	private boolean enableCommandGlist = true;
	@Getter
	@ConfigKey(comment = "是否可以使用“&”作为聊天格式化代码")
	private boolean colorableChat = true;
	@ConfigKey(comment = """
		设置命令别名
		broadcast：全局聊天
		local：本地聊天
		注：“vchat local” 仅对玩家可用
		修改并重载后玩家需要重新加入游戏才会生效""")
	private Config commandAlias = Config.wrap(Map.of(
		LOCAL, LOCAL_DEFAULT_ALIAS,
		BROADCAST, BROADCAST_DEFAULT_ALIAS
	), Config.inMemory().configFormat());
	@Getter
	private String commandBroadcastAlias;
	@Getter
	private String commandLocalAlias;

	protected VelocityChatConfig(@NotNull Path configPath) {
		super(configPath);
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	public void load() {
		super.load();
		boolean shouldSave = false;
		Config commandAlias = this.commandAlias;
		String broadcastAlias = commandAlias.get(BROADCAST);
		if (broadcastAlias == null) {
			commandAlias.set(BROADCAST, BROADCAST_DEFAULT_ALIAS);
			shouldSave = true;
		}
		String localAlias = commandAlias.get(LOCAL);
		if (localAlias == null) {
			commandAlias.set(LOCAL, LOCAL_DEFAULT_ALIAS);
			shouldSave = true;
		}
		if (shouldSave) {
			this.save();
		}
		commandBroadcastAlias = broadcastAlias;
		commandLocalAlias = localAlias;
	}
}
