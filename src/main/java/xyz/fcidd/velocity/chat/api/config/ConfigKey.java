package xyz.fcidd.velocity.chat.api.config;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigKey {
	String comment() default "";

	String path() default "";
}
