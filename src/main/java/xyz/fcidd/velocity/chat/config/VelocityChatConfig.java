package xyz.fcidd.velocity.chat.config;

import fun.qu_an.lib.basic.config.AbstractAnnotationConfig;
import fun.qu_an.lib.basic.config.ConfigKey;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public class VelocityChatConfig extends AbstractAnnotationConfig {
	public static final VelocityChatConfig CONFIG = new VelocityChatConfig(DATA_DIRECTORY.resolve("config.toml"));

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
	@ConfigKey(comment = "是否在ping时发送玩家列表")
	boolean sendPlayersOnPing = true;
	@Getter
	@ConfigKey(comment = "Tab列表是否显示全部群组玩家")
	boolean showGlobalTabList = false;

	public VelocityChatConfig(@NotNull Path configPath) {
		super(configPath);
	}

	/**
	 * 加载/重载配置文件
	 */
	@Override
	@SneakyThrows
	public void load() {
		super.load();
	}
}
