package fun.qu_an.minecraft.velocity.api.util;

import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import org.jetbrains.annotations.NotNull;

public class Utils {
	private final @NotNull ApiPlayerUtil apiPlayerUtil;
	private final @NotNull ApiTaskUtil apiTaskUtil;

	public Utils(@NotNull Qu_anVelocityApi qu_anVelocityApi) {
		apiPlayerUtil = new ApiPlayerUtil(qu_anVelocityApi);
		apiTaskUtil = new ApiTaskUtil(qu_anVelocityApi);
	}

	public @NotNull ApiPlayerUtil getProxyUtil() {
		return apiPlayerUtil;
	}

	public @NotNull ApiTaskUtil getTaskUtil() {
		return apiTaskUtil;
	}
}
