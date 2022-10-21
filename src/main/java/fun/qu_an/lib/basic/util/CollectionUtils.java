package fun.qu_an.lib.basic.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class CollectionUtils {
	/**
	 * 去重并删除指定元素
	 *
	 * @return 不可变的 Set 集合
	 */
	@SafeVarargs
	@Unmodifiable
	@NotNull
	public static <E> Set<E> removeToSet(Collection<E> set, E... valuesToRemove) {
		return mixToSet(set, List.of(valuesToRemove));
	}

	/**
	 * 对所有集合合并并去重
	 *
	 * @return 不可变的 Set 集合
	 */
	@SafeVarargs
	@Unmodifiable
	@NotNull
	public static <E> Set<E> mixToSet(@NotNull Collection<E> set1, Collection<E> @NotNull ... sets) {
		Set<E> result = new HashSet<>(set1);
		for (Collection<E> set : sets) {
			result.addAll(set);
		}
		return Set.copyOf(result);
	}
}
