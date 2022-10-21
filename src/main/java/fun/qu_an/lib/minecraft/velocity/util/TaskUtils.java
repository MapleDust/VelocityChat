package fun.qu_an.lib.minecraft.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fun.qu_an.lib.minecraft.velocity.Qu_anVelocityLib;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class TaskUtils {
	private static final ProxyServer proxyServer = Qu_anVelocityLib.getProxyServer();
	private static final Object DEFAULT_PLUGIN = Qu_anVelocityLib.getInstance();

	public static ScheduledTask delay(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return delay(DEFAULT_PLUGIN, time, unit, runnable);
	}

	public static ScheduledTask delay(@NotNull Object plugin, int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.delay(time, unit)
			.schedule();
	}

	public static ScheduledTask repeat(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return repeat(DEFAULT_PLUGIN, time, unit, runnable);
	}

	public static ScheduledTask repeat(@NotNull Object plugin, int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
