package xyz.fcidd.velocity.chat.util;

import java.util.concurrent.CompletableFuture;

public class FutureUtil {
	private static final CompletableFuture<Void> FUTURE = CompletableFuture.completedFuture(null);

	public static void thenRun(Runnable runnable) {
		FUTURE.thenRun(runnable);
	}
}
