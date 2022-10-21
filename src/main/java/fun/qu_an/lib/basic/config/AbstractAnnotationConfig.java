package fun.qu_an.lib.basic.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public abstract class AbstractAnnotationConfig {
	protected final CommentedFileConfig fileConfig;

	protected AbstractAnnotationConfig(@NotNull Path path) {
		this.fileConfig = AnnotationConfigs.defaultConfigBuilder(path).build();
	}

	protected AbstractAnnotationConfig(CommentedFileConfig fileConfig) {
		this.fileConfig = fileConfig;
	}

	/**
	 * 加载配置文件
	 */
	protected void load() {
		AnnotationConfigs.load(this, fileConfig);
	}

	private CompletableFuture<Void> lastFuture;

	/**
	 * 异步保存配置文件
	 */
	protected void saveAsync() {
		synchronized (this) {
			if (lastFuture == null) {
				lastFuture = CompletableFuture.completedFuture(null);
			}
			lastFuture = lastFuture.thenRun(() -> AnnotationConfigs.save(this, fileConfig));
		}
	}

	/**
	 * 保存配置文件
	 */
	protected void save() {
		AnnotationConfigs.save(this, fileConfig);
	}
}
