package xyz.fcidd.velocity.chat.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.util.List;

public class TomlConfigs {

	public static String getString(CommentedFileConfig config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Boolean getBoolean(CommentedFileConfig config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <T> List<T> getList(CommentedFileConfig config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Config getTable(CommentedFileConfig config, String path) {
		try {
			return config.get(path);
		} catch (ClassCastException e) {
			return null;
		}
	}
}
