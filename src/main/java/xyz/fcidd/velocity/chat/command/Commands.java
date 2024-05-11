package xyz.fcidd.velocity.chat.command;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.message.Translates;
import xyz.fcidd.velocity.chat.util.ComponentUtils;

import static xyz.fcidd.velocity.chat.util.Utils.PROXY_SERVER;

public class Commands {
	public static final String[] TELL = {"tell", "msg", "me"};
	public static final String[] TELEPORT = {"tp", "teleport"};
	public static final String GLOBAL = "global";
	public static final String GLOBAL_DEFAULT_ALIAS = "glb";
	public static final String LOCAL = "local";
	public static final String LOCAL_DEFAULT_ALIAS = "lcl";
	public static final String BROADCAST = "broadcast";
	public static final String BROADCAST_DEFAULT_ALIAS = "br";

	public static void tell(String player, Component messageComponent, CommandSource source) {
		// 发送私聊
		PROXY_SERVER.getPlayer(player).ifPresentOrElse(player1 -> {
			player1.sendMessage(Translates.TELL_MESSAGE.args(
				Translates.PROXY,
				messageComponent
			));
			// 发送反馈
			source.sendMessage(Translates.TELL_SUCCEED.args(
				ComponentUtils.getPlayerComponent(player1),
				messageComponent
			));
		}, () -> {
			// 发送反馈
			source.sendMessage(Translates.TELL_FAILED.args(Component.text(player)));
		});
	}
}
