package xyz.fcidd.velocity.chat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import xyz.fcidd.velocity.chat.config.LoadConfig;

import java.util.Map;
import java.util.Set;

public class PlayerChatListener {
    private final ProxyServer proxyServer;

    public PlayerChatListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void playerChatEvent(PlayerChatEvent playerChatEvent) {
        //获取玩家信息
        Player player = playerChatEvent.getPlayer();
        //获取玩家昵称
        String playerUsername = player.getUsername();
        //获取服务器昵称
        String serverName = player.getCurrentServer().get().getServer().getServerInfo().getName();
        //获取玩家发送的消息
        String playerMessage = playerChatEvent.getMessage().replaceAll("&", "§");
        //取消消息发送
        playerChatEvent.setResult(PlayerChatEvent.ChatResult.denied());
        //读取配置文件
        LoadConfig loadConfig = new LoadConfig();
        //获取配置文件的主前缀
        String mainPrefix = loadConfig.getMainPrefix();
        //获取所有配置文件的子服名称和子服前缀
        Map<String, Object> configServerList = loadConfig.getConfigServerList();
        //获取配置文件中服务器所有子服名称
        Set<String> configServers = configServerList.keySet();
        //进行循环
        configServers.forEach(configServer -> {
            //如果配置文件的服务器名称和玩家所在的服务器一致
            if (serverName.equals(configServer)) {
                //获取子服的前缀
                Object subPrefix = configServerList.get(configServer);
                //向所有服务器发送处理后的玩家消息
                proxyServer.getAllServers().forEach(registeredServer -> registeredServer.sendMessage(Component.text(subPrefix + mainPrefix + "§f<" + playerUsername + "> " + playerMessage)));
            }
        });
    }
}
