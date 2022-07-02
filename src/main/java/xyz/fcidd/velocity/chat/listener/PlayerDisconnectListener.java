package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class PlayerDisconnectListener {
    private final ProxyServer proxyServer;

    public PlayerDisconnectListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void playerDisconnectServer(DisconnectEvent event) {
        // 获取玩家信息
        Player player = event.getPlayer();
        // 获取玩家昵称
        String playerUsername = player.getUsername();
        // 将玩家退出群组的消息发送给所有人
        proxyServer.getAllServers().forEach(server -> {
            server.sendMessage(Component.text("§r" + playerUsername + " §2离开了群组"));
        });
    }
}
