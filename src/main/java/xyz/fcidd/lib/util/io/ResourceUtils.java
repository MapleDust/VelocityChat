package xyz.fcidd.lib.util.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static xyz.fcidd.lib.util.io.JarPathFormat.*;


public class ResourceUtils {
	public static @Nullable String readToString(@NotNull Class<?> target, String path) {
		InputStream is = getAsStream(target, path);
		if (is == null) {
			return null;
		}
		try (is; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			baos.writeBytes(is.readAllBytes());
			return baos.toString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}

	@Nullable
	public static InputStream getAsStream(@NotNull Class<?> target, String path) {
		return target.getClassLoader().getResourceAsStream(JarPathFormat.format(path, ENDS_WITHOUT_SLASH));
	}

	@Nullable
	public static URL getAsURL(@NotNull Class<?> target, String path) {
		return target.getClassLoader().getResource(JarPathFormat.format(path, ENDS_WITHOUT_SLASH));
	}

	/**
	 * 遍历jar包中输入的路径下的所有文件
	 *
	 * @param target   目标类，用于定位资源文件所在的 jar 包
	 * @param path     资源目录在 jar 包内的路径
	 * @param consumer 对路径下每个文件执行的逻辑
	 * @return 是否成功访问资源目录
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static boolean visitResourceFolder(@NotNull Class<?> target, String path, @NotNull BiConsumer<ZipFile, ZipEntry> consumer) {
		path = JarPathFormat.format(path, STARTS_WITHOUT_SLASH, ENDS_WITH_SLASH);
		URL resource = target.getProtectionDomain().getCodeSource().getLocation();
		String jar = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
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
			return false;
		}
		return true;
	}

	@Deprecated(forRemoval = true)
	public static @NotNull String formatPath(@NotNull String path) {
		return JarPathFormat.format(path, ENDS_WITH_SLASH);
	}

	@Deprecated(forRemoval = true)
	public static @NotNull String formatPath(@NotNull String path, boolean endsWithSlash) {
		return JarPathFormat.format(path, endsWithSlash ? ENDS_WITH_SLASH : ENDS_WITHOUT_SLASH);
	}
}
