package xyz.fcidd.velocity.chat;

import lombok.SneakyThrows;

import java.io.*;

public class Initialization {
    // 配置文件目录
    private static final File CONFIG_FILE = new File("./plugins/velocitychat/config.toml");
    // 配置文件夹
    private static final File CONFIG_FOLDER = new File("./plugins/velocitychat/");
    // 读取 resource 目录下的配置文件
    private static final InputStream CONFIG_RESOURCE = Initialization.class.getClassLoader().getResourceAsStream("config.toml");

    /**
     * 初始化插件
     */
    public static void init() {
        try {
            byte[] data = new byte[1024 * 10];
            int len;
            // 如果配置文件不存在
            if (!CONFIG_FILE.exists()) {
                // 创建配置文件夹
                CONFIG_FOLDER.mkdirs();

                // 创建配置文件
                RandomAccessFile raf = new RandomAccessFile(CONFIG_FILE, "rw");

                // 读取 resource 目录下的配置文件
                while ((len = CONFIG_RESOURCE.read(data)) != 1) {
                    // 写入配置文件
                    raf.write(data, 0, len);
                }
                // 关闭流
                CONFIG_RESOURCE.close();
                raf.close();
            }
        } catch (Exception e) {

        }

    }
}
