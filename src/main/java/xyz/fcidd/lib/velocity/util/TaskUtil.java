package xyz.fcidd.lib.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class TaskUtil {
	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull TaskUtil create(Object plugin, ProxyServer proxyServer) {
		return new TaskUtil(plugin, proxyServer);
	}

	private final ProxyServer proxyServer;
	private final Object plugin;

	TaskUtil(Object plugin, ProxyServer proxyServer) {
		this.plugin = plugin;
		this.proxyServer = proxyServer;
	}

	/**
	 * 计划执行延迟任务，使用默认插件实例创建
	 *
	 * @param time 延迟时间
	 * @param unit 时间单位
	 * @param runnable 计划任务
	 * @return 计划任务的实例
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ScheduledTask delay(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
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
	public ScheduledTask repeat(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
