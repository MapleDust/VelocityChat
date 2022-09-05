package xyz.fcidd.velocity.chat.util;

import java.util.List;

public class BasicUtil {
    /**
     * char数组中包含目标字符
     *
     * @param c 目标字符
     * @param chars char数组
     * @return 判断的结果
     */
    public static boolean matchAny(char c, char[] chars){
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
    public static boolean matchAny(String s, String[] strs){
        for (String str : strs) {
            if (str.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithAny(String s, String[] strs) {
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }

    public static boolean startsWithAny(String s, List<String> strs) {
        for (String str : strs) {
            if (s.startsWith(str)) return true;
        }
        return false;
    }
}
