package xyz.fcidd.velocity.chat.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class TextUtil {
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
     * String数组中包含目标字符串
     *
     * @param s 目标字符串
     * @param strs String数组
     * @return 判断结果
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

    @Contract(pure = true)
    public static boolean startsWithAny(@NotNull String s, String @NotNull ... strs) {
        if (strs.length == 1) return s.startsWith(strs[0]);
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }

    @Contract(pure = true)
    public static boolean startsWithAny(@NotNull String s, @NotNull List<String> strs) {
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }
}
