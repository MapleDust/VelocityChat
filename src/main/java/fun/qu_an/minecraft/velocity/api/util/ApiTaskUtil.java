package fun.qu_an.minecraft.velocity.api.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fun.qu_an.minecraft.velocity.api.Qu_anVelocityApi;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ApiTaskUtil {
	private final ProxyServer proxyServer;
	private final Object plugin;

	ApiTaskUtil(@NotNull Qu_anVelocityApi qu_anVelocityApi) {
		this.proxyServer = qu_anVelocityApi.getProxyServer();
		this.plugin = qu_anVelocityApi.getPlugin();
	}

	public ScheduledTask delay(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.delay(time, unit)
			.schedule();
	}

	public ScheduledTask repeat(int time, @NotNull TimeUnit unit, @NotNull Runnable runnable) {
		return proxyServer.getScheduler()
			.buildTask(plugin, runnable)
			.repeat(time, unit)
			.schedule();
	}
}
