package xyz.fcidd.velocity.chat.config;

import com.moandjiezana.toml.Toml;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Map;

@Data
public class LoadConfig {
    private String mainPrefix = loadToml().getString("mainprefix");
    private Map<String, Object> configServerList = loadToml().getTable("subprefix").toMap();

    @SneakyThrows
    public static Toml loadToml() {
        File configFile = new File("./plugins/velocitychat/config.toml");
        return new Toml().read(configFile);
    }
}
