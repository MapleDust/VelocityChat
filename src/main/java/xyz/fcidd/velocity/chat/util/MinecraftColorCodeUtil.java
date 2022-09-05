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
		return message.contains("&") ? message.replaceAll("&(?=[0-9a-fk-or])", "§").replace("&\\", "&") : message;
	}
}
