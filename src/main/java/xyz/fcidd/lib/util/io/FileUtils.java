package xyz.fcidd.lib.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static xyz.fcidd.lib.util.io.JarPathFormat.ENDS_WITH_SLASH;

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

	public static FileSystem getZipFileSystem(@NotNull Path zipPath, boolean create) throws IOException {
		return FileSystems.newFileSystem(
			URI.create("jar:file:" + zipPath.toUri().getPath()),
			create ? Map.of("create", "true") : Map.of()
		);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isFile(@NotNull Path path) {
		if (path.getNameCount() == 0) {
			return false;
		}
		File file = path.toFile();
		return file.isFile();
	}

	public static void requireIsFile(@NotNull Path path) {
		if (!isFile(path)) {
			throw new IllegalArgumentException("Not a file path " + path);
		}
	}

	public static void requireIsFile(@NotNull Path path, String message) {
		if (!isFile(path)) {
			throw new IllegalArgumentException(message);
		}
	}

	@Deprecated(forRemoval = true)
	public static void visitResourceFolder(@NotNull Class<?> target, String path, @NotNull BiConsumer<ZipFile, ZipEntry> consumer) {
		ResourceUtils.visitResourceFolder(target, path, consumer);
	}

	@Deprecated(forRemoval = true)
	public static @NotNull String formatPath(@NotNull String path) {
		return JarPathFormat.format(path, ENDS_WITH_SLASH);
	}
}
