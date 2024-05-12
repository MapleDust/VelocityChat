package xyz.fcidd.lib.config;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigKey {
	/**
	 * 设置注解字段的配置文件注释，支持文本块
	 *
	 * @return 配置文件注释
	 */
	String comment() default "";

	/**
	 * 设置注解字段的配置文件路径并设置配置项名称，留空时使用字段名自动生成
	 *
	 * @return 配置文件路径
	 */
	String path() default "";

	Comment[] comments() default {};
}
