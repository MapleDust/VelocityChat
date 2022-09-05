package xyz.fcidd.velocity.chat.util;

public class MinecraftColorCodeUtil {
	private static final char[] colorCode = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

	/**
	 * 处理玩家发送过来的带有我的世界颜色代码消息
	 *
	 * @param message 玩家发送的消息
	 * @return 处理过后的玩家消息
	 */
	public static String replaceColorCode(String message) {
		// 性能优化，不包含则直接返回自身
		if (!message.contains("&")) return message;

		StringBuilder builder = new StringBuilder();
		// 将消息转换为char数组
		char[] messageChars = message.toCharArray();
		// 遍历判断char数组除了最后一位之外的字符是不是“&”
		for (int i = 0, length = messageChars.length - 1; i < length; i++) {
			// 如果char数组中的一个字符等于&并且下一个字符等于颜色代码列表
			if (messageChars[i] == '&' && BasicUtil.matchAny(messageChars[i + 1], colorCode)) {
				// 将我的世界颜色字符添加到字符后面
				builder.append('§');
			} else {
				// 否则将字符追加
				builder.append(messageChars[i]);
			}
		}
		// 追加最后一个字符
		builder.append(messageChars[messageChars.length - 1]);
		return builder.toString();

		// 性能低下，弃用
//		return message.contains("&") ? message.replaceAll("(?<!\\\\)&(?=[0-9a-fk-or])", "§") : message;

	}
}
