package fun.qu_an.lib.minecraft.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fun.qu_an.lib.minecraft.velocity.Qu_anVelocityLib;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class TaskUtils {
	private static final ProxyServer proxyServer = Qu_anVelocityLib.getProxyServer();
	private static final Object DEFAULT_PLUGIN = Qu_anVelocityLib.getLibPlugin();

	/**
	 * 计划执行延迟任务，使用默认插件实例创建
	 *
	 * @param time 延迟时间
	 * @param unit 时间单位
	 * @param runnable 计划任务
	 * @return 计划任务的实例
	 */
	public static ScheduledTask delay(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return delay(DEFAULT_PLUGIN, time, unit, runnable);
	}

	/**
	 * 计划执行延迟任务
	 *
	 * @param plugin 用于创建任务的插件实例
	 * @param time 延迟时间
	 * @param unit 时间单位
	 * @param runnable 计划任务
	 * @return 计划任务的实例
	 */
	public static ScheduledTask delay(@NotNull Object plugin, int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.delay(time, unit)
			.schedule();
	}

	/**
	 * 计划执行重复任务，使用默认插件实例创建
	 *
	 * @param time 间隔时间
	 * @param unit 时间单位
	 * @param runnable 计划任务
	 * @return 计划任务的实例
	 */
	public static ScheduledTask repeat(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return repeat(DEFAULT_PLUGIN, time, unit, runnable);
	}

	/**
	 * 计划执行重复任务
	 *
	 * @param plugin 用于创建任务的插件实例
	 * @param time 延迟时间
	 * @param unit 时间单位
	 * @param runnable 计划任务
	 * @return 计划任务的实例
	 */
	public static ScheduledTask repeat(@NotNull Object plugin, int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
