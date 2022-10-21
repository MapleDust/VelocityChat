package fun.qu_an.lib.basic.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 文本相关工具
 */
@SuppressWarnings("unused")
public class TextUtils {
    /**
     * char数组中包含目标字符
     *
     * @param c 目标字符
     * @param chars char数组
     * @return 判断的结果
     */
    @Contract(pure = true)
    public static boolean equalsAny(char c, char @NotNull ... chars){
        for (char aChar : chars) {
            if (c == aChar) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否与任意给定的字符串相同
     *
     * @param s 待判断的字符串
     * @param strs 定的字符串
     * @return 字符串是否与任意给定的字符串相同
     */
    @Contract(pure = true)
    public static boolean equalsAny(@NotNull String s, String @NotNull ... strs){
        if (strs.length == 1) return s.startsWith(strs[0]);
        for (String str : strs) {
            if (str.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否以任意给定的字符串开头
     *
     * @param s 待判断的字符串
     * @param strs 定的字符串
     * @return 字符串是否以任意给定的字符串开头
     */
    @Contract(pure = true)
    public static boolean startsWithAny(@NotNull String s, String @NotNull ... strs) {
        if (strs.length == 1) return s.startsWith(strs[0]);
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }


    /**
     * 判断字符串是否以任意给定的字符串开头
     *
     * @param s 待判断的字符串
     * @param strs 定的字符串
     * @return 字符串是否以任意给定的字符串开头
     */
    @Contract(pure = true)
    public static boolean startsWithAny(@NotNull String s, @NotNull List<String> strs) {
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }

    /**
     * 判断字符串是否以任意给定的字符串结尾
     *
     * @param s 待判断的字符串
     * @param strs 定的字符串
     * @return 字符串是否以任意给定的字符串结尾
     */
    @Contract(pure = true)
    public static boolean endsWithAny(@NotNull String s, String @NotNull ... strs) {
        if (strs.length == 1) return s.startsWith(strs[0]);
        for (String str : strs) {
            if (s.endsWith(str)) return true;
        }
        return false;
    }

    /**
     * 判断字符串是否以任意给定的字符串结尾
     *
     * @param s 待判断的字符串
     * @param strs 定的字符串
     * @return 字符串是否以任意给定的字符串结尾
     */
    @Contract(pure = true)
    public static boolean endsWithAny(@NotNull String s, @NotNull List<String> strs) {
        for (String str : strs) {
            if (s.endsWith(str)) return true;
        }
        return false;
    }
}
