package fun.qu_an.lib.basic.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * 序列化工具
 */
@SuppressWarnings("unused")
public class SerializeUtils {
	/**
	 * 将对象写入文件
	 *
	 * @param o    待写入的对象
	 * @param file 目标文件，会自动创建和覆盖
	 */
	public static void write(Object o, @NotNull File file) throws IOException {
		FileUtils.createFileAndDirs(file);
		try (FileOutputStream fos = new FileOutputStream(file);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(o);
		}
	}

	/**
	 * 从文件中读取对象
	 *
	 * @return 读取的对象
	 */
	public static @Nullable Object readObject(@NotNull File file) throws IOException, ClassNotFoundException {
		if (!file.exists()) return null;
		try (FileInputStream fis = new FileInputStream(file);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			return ois.readObject();
		}
	}

	/**
	 * 从文件中读取对象
	 *
	 * @return 对象的 byte 数组
	 */
	public static byte @Nullable [] read(@NotNull File file) throws IOException {
		if (!file.exists()) return null;
		try (FileInputStream fis = new FileInputStream(file)) {
			return fis.readAllBytes();
		}
	}

	/**
	 * 将对象序列化为 byte 数组
	 */
	public static byte @NotNull [] serialize(Object o) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(o);
			return (baos.toByteArray());
		}
	}

	/**
	 * 将 byte 数组反序列化为对象
	 */
	public static Object deserialize(byte @NotNull [] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			 ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		}
	}
}
