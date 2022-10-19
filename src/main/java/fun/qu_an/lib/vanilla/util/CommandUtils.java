package fun.qu_an.lib.vanilla.util;

import fun.qu_an.lib.basic.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class CommandUtils {
	public static final String[] EXECUTE = {"execute"};

	public static int indexOfNode(@NotNull List<String> command, String @NotNull ... nodes) {
		for (String node : nodes) {
			int i = command.indexOf(node);
			if (i >= 0) return i;
		}
		return -1;
	}

	public static int indexOfRoot(@NotNull List<String> command, String @NotNull ... roots) {
		for (String root : roots) {
			String s0 = command.get(0);
			if (s0.equals(root)) return command.indexOf(root);
			if (TextUtils.equalsAny(s0, EXECUTE)
				&& command.contains(root)) {
				return command.indexOf(root);
			}
		}
		return -1;
	}

	public static boolean is(@NotNull List<String> command, String @NotNull ... roots) {
		for (String root : roots) {
			String s0 = command.get(0);
			if (s0.equals(root)) return true;
			if (TextUtils.equalsAny(s0, EXECUTE)
				&& command.contains(root)) {
				return true;
			}
		}
		return false;
	}
}
