package xyz.fcidd.velocity.chat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fcidd.velocity.chat.VelocityChatPlugin;

public class ILogger {
	public static final Logger LOGGER = VelocityChatPlugin.getLogger();
	public static final Logger CHAT_LOGGER = LoggerFactory.getLogger("chat");
	public static final Logger COMMAND_LOGGER = LoggerFactory.getLogger("command");
}
