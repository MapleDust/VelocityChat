package fun.qu_an.lib.basic.util;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class JsonUtils {
	public static final Gson FORMATTED_GSON = new GsonBuilder()
		.setPrettyPrinting()
		.create();

	/**
	 * 从指定路径读取Json
	 */
	public static JsonElement read(@NotNull Path json) throws IOException {
		File jsonFile = json.toFile();
		if (!jsonFile.exists()) {
			JsonArray jsonArray = new JsonArray();
			write(json, jsonArray);
			return jsonArray;
		}
		if (!jsonFile.isFile()) {
			throw new FileNotFoundException(jsonFile.getPath());
		}
		try (FileReader fileReader = new FileReader(jsonFile)) {
			return JsonParser.parseReader(fileReader).getAsJsonArray();
		} catch (IllegalStateException e) {
			return JsonNull.INSTANCE;
		}
	}

	/**
	 * 将Json写入指定文件
	 */
	public static void write(@NotNull Path path, Object json) throws IOException {
		File jsonFile = path.toFile();
		File parentFile = jsonFile.getParentFile();
		if (!jsonFile.exists()
			&& !parentFile.exists()
			&& !parentFile.mkdirs()
			&& !jsonFile.createNewFile()) {
			throw new FileSystemException("创建失败");
		} else if (!jsonFile.isFile()
			&& !jsonFile.delete()
			&& !jsonFile.createNewFile()) {
			throw new FileSystemException("创建失败");
		}
		String jsonString = FORMATTED_GSON.toJson(json);
		FileUtils.write(path, jsonString);
	}
}
