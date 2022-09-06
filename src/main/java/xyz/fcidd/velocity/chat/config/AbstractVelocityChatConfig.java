package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public abstract class AbstractVelocityChatConfig {
	protected CommentedFileConfig config;

	public AbstractVelocityChatConfig(CommentedFileConfig config) {
		this.config = config;
	}

	public void load() {
		VelocityChatConfigs.load(this, config);
	}

	protected void save() {
		config.save();
	}
}
