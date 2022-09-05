package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import xyz.fcidd.velocity.chat.config.annotation.TomlConfig;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

@TomlConfig
public abstract class AbstractTomlConfig implements Serializable {
	protected transient final Path tomlPath;
	protected transient Toml toml;

	AbstractTomlConfig(Toml toml, Path tomlPath) {
		this.tomlPath = tomlPath;
		this.toml = toml;
	}

	public final <T> T get(String key) {
		return TomlConfigs.get(toml, key);
	}

	public final String getString(String key) {
		return TomlConfigs.getString(toml, key);
	}

	public final Boolean getBoolean(String key) {
		return TomlConfigs.getBoolean(toml, key);
	}

	public final <T> List<T> getList(String key) {
		return TomlConfigs.getList(toml, key);
	}

	public final Toml getTable(String key) {
		return TomlConfigs.getTable(toml, key);
	}

	public final List<Toml> getTables(String key) {
		return TomlConfigs.getTables(toml, key);
	}

	public final void save() {
		TomlConfigs.save(this, tomlPath);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractTomlConfig that)) return false;

		if (!tomlPath.equals(that.tomlPath)) return false;
		return toml.equals(that.toml);
	}

	@Override
	public int hashCode() {
		int result = tomlPath.hashCode();
		result = 31 * result + toml.hashCode();
		return result;
	}
}
