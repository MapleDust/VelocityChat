package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.listener.PlayerChatListener;
import xyz.fcidd.velocity.chat.listener.PlayerDisconnectListener;
import xyz.fcidd.velocity.chat.listener.PlayerLoginServerListener;

@Plugin(id = "velocity_chat", name = "VelocityChat", version = "1.2.0",
        authors = "MapleDust", url = "https://github.com/MapleDust/VelocityChat")
public class VelocityChatPlugin {
    @Getter
    private static ProxyServer PROXY_SERVER;
    @Getter
    private static Logger LOGGER;

    @Inject
    public VelocityChatPlugin(ProxyServer proxyServer, Logger logger) {
        PROXY_SERVER = proxyServer;
        LOGGER = logger;
        // 初始化插件
        Initialization.init();
        logger.info("§aVelocityChat 已载入完成,项目地址 https://github.com/MapleDust/VelocityChat");
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // 注册玩家聊天消息监听器
        PROXY_SERVER.getEventManager().register(this, new PlayerChatListener(PROXY_SERVER));
        // 注册玩家连接服务器监听器
        PROXY_SERVER.getEventManager().register(this, new PlayerLoginServerListener(PROXY_SERVER));
        // 注册玩家断开服务器监听器
        PROXY_SERVER.getEventManager().register(this, new PlayerDisconnectListener(PROXY_SERVER));
    }


}
