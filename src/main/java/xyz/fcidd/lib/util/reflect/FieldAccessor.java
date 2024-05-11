package xyz.fcidd.lib.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class FieldAccessor {
	private final @Nullable Object parentObj;
	private final @NotNull Field field;
	private final int modifiers;
	private final boolean originalAccessible;

	public FieldAccessor(@Nullable Object parentObj, @NotNull Field field) {
		this.field = field;
		this.modifiers = field.getModifiers();
		if (isStatic()) {
			this.parentObj = null;
			originalAccessible = field.canAccess(null);
		} else {
			this.parentObj = parentObj;
			originalAccessible = field.canAccess(parentObj);
		}
	}

	public Object get() {
		Object o;
		synchronized (field) {
			field.setAccessible(true);
			try {
				o = field.get(parentObj);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} finally {
				field.setAccessible(originalAccessible);
			}
		}
		return o;
	}

	public void set(Object value) {
		synchronized (field) {
			field.setAccessible(true);
			try {
				field.set(parentObj, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} finally {
				field.setAccessible(originalAccessible);
			}
		}
	}

	public void apply(@NotNull Consumer<Field> consumer) {
		synchronized (field) {
			field.setAccessible(true);
			try {
				consumer.accept(field);
			} finally {
				field.setAccessible(originalAccessible);
			}
		}
	}

	public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
		field.setAccessible(true);
		T annotation;
		try {
			annotation = field.getAnnotation(annotationClass);
		} finally {
			field.setAccessible(originalAccessible);
		}
		return annotation;
	}

	public boolean isTransient() {
		return Modifier.isTransient(modifiers);
	}

	public boolean isFinal() {
		return Modifier.isFinal(modifiers);
	}

	public boolean isStatic() {
		return Modifier.isStatic(modifiers);
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(modifiers);
	}

	public boolean isNative() {
		return Modifier.isNative(modifiers);
	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isPrivate() {
		return Modifier.isPrivate(modifiers);
	}

	public boolean isProtected() {
		return Modifier.isProtected(modifiers);
	}

	public boolean isPublic() {
		return Modifier.isPublic(modifiers);
	}

	public boolean isStrict() {
		return Modifier.isStrict(modifiers);
	}

	public boolean isSynchronized() {
		return Modifier.isSynchronized(modifiers);
	}

	public boolean isVolatile() {
		return Modifier.isVolatile(modifiers);
	}
}
