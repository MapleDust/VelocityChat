package fun.qu_an.lib.basic.config;

import java.lang.annotation.*;

/**
 * 用于将成员变量标记为配置文件中的键
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigKey {
	/**
	 * 设置该键的注释，可以是多行注释，新行不需要以“#”开头
	 */
	String comment() default "";

	/**
	 * 键路径
	 */
	String path() default "";
}
