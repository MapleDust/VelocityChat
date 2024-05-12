package xyz.fcidd.lib.config;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Comment {
	String path() default "";
	String comment() default "";
}
