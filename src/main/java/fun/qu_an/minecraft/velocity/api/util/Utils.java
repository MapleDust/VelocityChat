package fun.qu_an.minecraft.velocity.api.util;

import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import org.jetbrains.annotations.NotNull;

public class Utils {
	private final @NotNull ProxyUtil proxyUtil;
	private final @NotNull TaskUtil taskUtil;

	public Utils(@NotNull Qu_anVelocityApi qu_anVelocityApi) {
		proxyUtil = new ProxyUtil(qu_anVelocityApi);
		taskUtil = new TaskUtil(qu_anVelocityApi);
	}

	public @NotNull ProxyUtil getProxyUtil() {
		return proxyUtil;
	}

	public @NotNull TaskUtil getTaskUtil() {
		return taskUtil;
	}
}
