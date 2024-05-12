package xyz.fcidd.lib.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
	/**
	 * 遍历输入的目录下第一层的所有文件
	 */
	public static void forEachChild(@NotNull Path folderPath, @NotNull Consumer<File> fileConsumer) {
		File[] files = folderPath.toFile().listFiles();
		if (files == null) return;
		for (File file : files) {
			fileConsumer.accept(file);
		}
	}

	/**
	 * 复制文件，不带缓存，一次性读取
	 */
	public static boolean copyWithoutBuffer(@NotNull File from, @NotNull File to) throws IOException {
		if (!from.exists()) return false;
		if (!create(to)) return false;
		try (InputStream is = new FileInputStream(from);
			 OutputStream os = new FileOutputStream(to)
		) {
			os.write(is.readAllBytes());
		}
		return true;
	}

	/**
	 * 解压文件，不带缓存，一次性读取
	 */
	public static void unpack(@NotNull ZipFile zipFile, @NotNull ZipEntry zipEntry, @NotNull File to) throws IOException {
		if (!create(to)) return;
		try (InputStream is = zipFile.getInputStream(zipEntry);
			 OutputStream os = new FileOutputStream(to)) {
			os.write(is.readAllBytes());
		}
	}

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
