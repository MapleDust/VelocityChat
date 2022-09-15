package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import xyz.fcidd.velocity.chat.config.annotation.ConfigObject;

@ConfigObject
public abstract class AbstractVcConfig {
	protected CommentedFileConfig config;

	public AbstractVcConfig(CommentedFileConfig config) {
		this.config = config;
	}

	public void load() {
		VcConfigs.load(this, config);
	}

	protected void save() {
		config.save();
	}
}
