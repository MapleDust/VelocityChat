package fun.qu_an.lib.basic.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@SuppressWarnings("unused")
public class Result {
	private static final Result SUCCEED = new Result(null, Action.SUCCEED);
	private static final Result NO_EFFECT = new Result(null, Action.NO_EFFECT);
	private static final Result FAILURE = new Result(null, Action.FAILURE);
	private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);
	private final @Nullable String message;
	private final @NotNull Action action;
	private final Result subResult;

	protected Result(@Nullable String message, @NotNull Action action) {
		this(message, action, null);
	}

	protected Result(@Nullable String message, Action action, Result subResult) {
		this.message = message;
		this.action = Objects.requireNonNull(action);
		this.subResult = subResult;
	}

	public Result subResult(Result subResult) {
		return new Result(message, action, subResult);
	}

	public boolean hasSubResult() {
		return subResult != null;
	}

	public Result subResult() {
		return subResult;
	}

	public boolean isSucceed() {
		return this.action == Action.SUCCEED;
	}

	public boolean isFailure() {
		return this.action == Action.FAILURE;
	}

	public boolean isNoEffect() {
		return this.action == Action.NO_EFFECT;
	}

	public String message() {
		return message == null ? "" : message;
	}

	public static Result succeed() {
		return SUCCEED;
	}

	public static Result succeed(String reason) {
		return reason == null || reason.equals("") ? SUCCEED : new Result(reason, Action.SUCCEED);
	}

	public static Result noEffect() {
		return NO_EFFECT;
	}

	public static Result noEffect(String reason) {
		return reason == null || reason.equals("") ? NO_EFFECT : new Result(reason, Action.NO_EFFECT);
	}

	public static Result failure() {
		return FAILURE;
	}

	public static Result failure(String reason) {
		return "".equals(reason) ? FAILURE : new Result(reason, Action.FAILURE);
	}

	public static Result exception(Throwable throwable) {
		LOGGER.error(throwable.getMessage(), throwable);
		return failure(throwable.getClass().getName() + ": " + throwable.getMessage());
	}

	@Override
	public String toString() {
		return action.name() + (message == null ? null : ": " + message);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Result result)) return false;

		if (!Objects.equals(message, result.message)) return false;
		if (action != result.action) return false;
		return Objects.equals(subResult, result.subResult);
	}

	@Override
	public int hashCode() {
		int result = message != null ? message.hashCode() : 0;
		result = 31 * result + action.hashCode();
		result = 31 * result + (subResult != null ? subResult.hashCode() : 0);
		return result;
	}

	private enum Action {
		SUCCEED, NO_EFFECT, FAILURE
	}
}