package xyz.fcidd.lib.config;

import org.jetbrains.annotations.NotNull;
import xyz.fcidd.lib.util.reflect.FieldAccessor;

import java.util.Map;

public record ConfigFieldRecord(@NotNull FieldAccessor accessor, @NotNull String path, @NotNull String comment, @NotNull Map<String, String> otherComments) {
}
