package xyz.fcidd.velocity.chat.config.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Comment {
	String[] value() default {};
}
