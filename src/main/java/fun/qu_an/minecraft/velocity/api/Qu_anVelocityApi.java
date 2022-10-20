package fun.qu_an.minecraft.velocity.api;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.qu_an.minecraft.velocity.api.language.LanguageLoader;
import fun.qu_an.minecraft.velocity.api.util.ProxyUtil;
import fun.qu_an.minecraft.velocity.api.util.TaskUtil;
import fun.qu_an.minecraft.velocity.api.util.Utils;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Qu_anVelocityApi {
	@Contract("_, _ -> new")
	public static @NotNull Qu_anVelocityApi create(@NotNull ProxyServer proxyServer, @NotNull Object plugin) {
		Objects.requireNonNull(proxyServer);
		Objects.requireNonNull(plugin);
		return new Qu_anVelocityApi(proxyServer, plugin);
	}

	private final ProxyServer proxyServer;
	private final Object plugin;
	private final @NotNull Utils utils;
	private Map<Key, LanguageLoader> langLoaders;

	private Qu_anVelocityApi(ProxyServer proxyServer, Object plugin) {
		this.proxyServer = proxyServer;
		this.plugin = plugin;
		utils = new Utils(this);
	}

	public ProxyServer getProxyServer() {
		return proxyServer;
	}

	public Object getPlugin() {
		return plugin;
	}

	public ProxyUtil getProxyUtil() {
		return utils.getProxyUtil();
	}

	public TaskUtil getTaskUtil() {
		return utils.getTaskUtil();
	}

	public @NotNull LanguageLoader getLanguageLoader(Key translationKey, @NotNull Path langsFolder, String langsPathInJar) {
		if (langLoaders != null) {
			LanguageLoader languageLoader = langLoaders.get(translationKey);
			if (languageLoader != null) return languageLoader;
		} else {
			langLoaders = new HashMap<>();
		}
		LanguageLoader languageLoader = LanguageLoader
			.create(plugin, translationKey, langsFolder, langsPathInJar);
		langLoaders.put(translationKey, languageLoader);
		return languageLoader;
	}
}
