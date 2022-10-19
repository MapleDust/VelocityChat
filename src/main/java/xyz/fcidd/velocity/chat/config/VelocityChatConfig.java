package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import fun.qu_an.lib.basic.api.config.AbstractAnnotationConfig;
import fun.qu_an.lib.basic.api.config.ConfigKey;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static xyz.fcidd.velocity.chat.VelocityChatPlugin.DATA_DIRECTORY;

@SuppressWarnings("FieldMayBeFinal")
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
	@ConfigKey(comment = """
		Tab列表是否显示全部群组玩家
		** 暂不推荐使用，会影响指令提示 **""")
	boolean showGlobalTabList = false;

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

	/**
	 * 加载/重载配置文件
	 */
	@Override
	@SneakyThrows
	public void load() {
		super.load();
	}
}
