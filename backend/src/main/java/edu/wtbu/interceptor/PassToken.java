package edu.wtbu.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义通过token注解，如果不加改注解直接拦截
//说明该注解可用于方法，接口、类、枚举、注解
@Target({ElementType.METHOD,ElementType.TYPE})
//该类型Annotations将被JVM保留，所以能在运行时被JVM或其他使用反射机制的代码所读取和使用
@Retention(RetentionPolicy.RUNTIME)
public @interface PassToken {
    //注解本质是一个接口，属性是接口的抽象方法，返回值类型 属性名称() [default 默认值]
    boolean required() default true;
}
