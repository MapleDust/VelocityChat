package xyz.fcidd.velocity.chat.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class MessageTaskUtil {
	private static final Executor MESSAGE_SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

	@Contract("_ -> new")
	public static @NotNull CompletableFuture<Void> runInMessageThread(@NotNull Runnable runnable) {
		return CompletableFuture.runAsync(runnable, MESSAGE_SINGLE_THREAD_EXECUTOR);
	}
}
