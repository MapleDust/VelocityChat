package fun.qu_an.lib.basic.api.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@SuppressWarnings("unused")
public abstract class AbstractAnnotationConfig {
	protected final CommentedFileConfig fileConfig;

	protected AbstractAnnotationConfig(@NotNull Path path) {
		this.fileConfig = AnnotationConfigs.defaultConfigBuilder(path).build();
	}

	protected AbstractAnnotationConfig(CommentedFileConfig fileConfig) {
		this.fileConfig = fileConfig;
	}

	protected void load() {
		AnnotationConfigs.load(this, fileConfig);
	}

	protected void save() {
		AnnotationConfigs.save(this, fileConfig);
	}
}
