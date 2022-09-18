package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import xyz.fcidd.velocity.chat.config.annotation.ConfigObject;

@ConfigObject
public abstract class AbstractConfig {
	protected CommentedFileConfig config;

	public AbstractConfig(CommentedFileConfig config) {
		this.config = config;
	}

	public void load() {
		Configs.load(this, config);
	}

	protected void save() {
		config.save();
	}
}
