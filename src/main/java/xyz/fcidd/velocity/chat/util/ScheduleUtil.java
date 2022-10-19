package xyz.fcidd.velocity.chat.util;

import com.velocitypowered.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

import static xyz.fcidd.velocity.chat.util.PluginUtil.PLUGIN;
import static xyz.fcidd.velocity.chat.util.PluginUtil.PROXY_SERVER;

@SuppressWarnings("unused")
public class ScheduleUtil {
	private static final Executor MESSAGE_EXECUTOR = Executors.newSingleThreadExecutor();

	public static synchronized void messageThread(@NotNull Runnable runnable) {
		CompletableFuture.runAsync(runnable, MESSAGE_EXECUTOR);
	}

	@SuppressWarnings("UnusedReturnValue")
	public static ScheduledTask delay(@NotNull Runnable runnable, int time, @NotNull TimeUnit unit) {
		return PROXY_SERVER.getScheduler()
			.buildTask(PLUGIN, runnable)
			.delay(time, unit)
			.schedule();
	}

	@SuppressWarnings("UnusedReturnValue")
	public static ScheduledTask repeat(@NotNull Runnable runnable, int time, @NotNull TimeUnit unit) {
		return PROXY_SERVER.getScheduler()
			.buildTask(PLUGIN, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
