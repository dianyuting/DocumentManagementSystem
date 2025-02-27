package edu.wtbu.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                //拦截所有请求
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/headimage/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {// 加入头像文件夹映射 可通过 localhost:9090/headimage/....   访问到指定位置的图片
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/"); // 默认头像
        registry.addResourceHandler("/headimage/**").addResourceLocations("file:upload/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Bean
    public MyJwtInterceptor jwtInterceptor(){
        return new MyJwtInterceptor();
    }
}
