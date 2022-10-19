package fun.qu_an.lib.velocity.util;

import com.velocitypowered.api.scheduler.ScheduledTask;
import fun.qu_an.lib.velocity.Qu_anLibPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class TaskUtils {
	private static final Qu_anLibPlugin PLUGIN = Qu_anLibPlugin.getInstance();

	public static ScheduledTask delay(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return PluginUtils.PROXY_SERVER.getScheduler()
			.buildTask(PLUGIN, runnable)
			.delay(time, unit)
			.schedule();
	}

	public static ScheduledTask repeat(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return PluginUtils.PROXY_SERVER.getScheduler()
			.buildTask(PLUGIN, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
