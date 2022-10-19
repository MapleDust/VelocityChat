package fun.qu_an.lib.basic.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("unused")
public class FileUtils {
	/**
	 * 创建输入的文件和路径
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void createFileAndDirs(@NotNull Path path) {
		File file = path.toFile();
		if (file.exists()) return;
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将文本写入指定路径的文件
	 */
	public static void write(@NotNull Path path, @NotNull String text) throws IOException {
		try (FileWriter fileWriter = new FileWriter(path.toFile())) {
			fileWriter.write(text);
		}
	}

	/**
	 * 遍历输入的路径下的所有文件
	 */
	public static void forEachChild(@NotNull Path folderPath, @NotNull Consumer<File> fileConsumer) {
		File[] files = folderPath.toFile().listFiles();
		if (files == null) return;
		for (File file : files) {
			fileConsumer.accept(file);
		}
	}

	/**
	 * 遍历jar包中输入的路径下的所有文件
	 */
	public static void visitResourceFolder(@NotNull Class<?> target, String path, @NotNull BiConsumer<ZipFile, ZipEntry> consumer) {
		path = formatPath(path);
		URL resource = target.getProtectionDomain().getCodeSource().getLocation();
		String jar = resource.getFile();
		try (ZipFile zipFile = new ZipFile(jar)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry entry;
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith(path) && !entry.isDirectory()) {
					consumer.accept(zipFile, entry);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static @NotNull String formatPath(@NotNull String path) {
		path = path.replace("\\", "/");
		if (path.startsWith("/")) path = path.substring(1);
		if (!path.endsWith("/")) path += "/";
		return path;
	}

	/**
	 * 复制文件，不带缓存，一次性读取
	 */
	public static boolean copyWithoutBuffer(@NotNull File from, @NotNull File to) {
		if (!from.exists()) return false;
		if (!create(to)) return false;
		try (InputStream is = new FileInputStream(from);
			 OutputStream os = new FileOutputStream(to)
		) {
			os.write(is.readAllBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * 解压文件，不带缓存，一次性读取
	 */
	public static void unpackWithoutBuffer(@NotNull ZipFile zipFile, @NotNull ZipEntry zipEntry, @NotNull File to) throws IOException {
		if (!create(to)) return;
		try (InputStream is = zipFile.getInputStream(zipEntry);
			 OutputStream os = new FileOutputStream(to)) {
			os.write(is.readAllBytes());
		}
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
	public static boolean create(@NotNull File file) {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
}
