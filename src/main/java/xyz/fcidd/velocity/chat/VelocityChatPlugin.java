package xyz.fcidd.velocity.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.fcidd.velocity.chat.listener.PlayerChatListener;

@Plugin(id = "velocity_chat", name = "VelocityChat", version = "1.0.0",
        authors = "MapleDust", url = "https://github.com/MapleDust/VelocityChat")
public class VelocityChatPlugin {
    private final ProxyServer proxyServer;

    @Inject
    public VelocityChatPlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        // 初始化插件
        Initialization.init();
        logger.info("§aVelocityChat 已载入完成,项目地址 https://github.com/MapleDust/VelocityChat");
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // 注册监听器
        proxyServer.getEventManager().register(this, new PlayerChatListener(proxyServer));
    }


}
