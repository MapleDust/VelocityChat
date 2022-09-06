package xyz.fcidd.velocity.chat.config.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigKey {
	String comment() default "";

	String parent() default "";
}
