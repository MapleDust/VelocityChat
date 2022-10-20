package fun.qu_an.basic.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * 序列化工具
 */
@SuppressWarnings("unused")
public class SerializeUtils {
	/**
	 *
	 * @param o 待写入的对象
	 * @param file 目标文件，会自动创建和覆盖
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void write(Object o, @NotNull File file) throws IOException {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) parent.mkdirs();
			file.createNewFile();
		}
		try (FileOutputStream fos = new FileOutputStream(file);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(o);
		}
	}

	public static @Nullable Object readObject(@NotNull File file) throws IOException, ClassNotFoundException {
		if (!file.exists()) return null;
		try (FileInputStream fis = new FileInputStream(file);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			return ois.readObject();
		}
	}

	public static byte @Nullable [] read(@NotNull File file) throws IOException {
		if (!file.exists()) return null;
		try (FileInputStream fis = new FileInputStream(file)) {
			return fis.readAllBytes();
		}
	}

	public static byte @NotNull [] serialize(Object o) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(o);
			return (baos.toByteArray());
		}
	}

	public static Object deserialize(byte @NotNull [] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			 ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		}
	}
}
