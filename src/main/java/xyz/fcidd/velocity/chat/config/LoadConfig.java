package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

@Data
public class LoadConfig implements Serializable {
    private String mainPrefix = loadToml().getString("main_prefix");
    private Map<String, Object> configServerList = loadToml().getTable("sub_prefix").toMap();
    private String mcdrCommandPrefix=loadToml().getString("mcdr_command_prefix");

    @SneakyThrows
    public static Toml loadToml() {
        File configFile = new File("./plugins/velocitychat/config.toml");
        return new Toml().read(configFile);
    }
}
