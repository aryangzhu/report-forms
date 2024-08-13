package com.example.core.report.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author ：DELL
 * @description：TODO
 * @date ：2020/2/15 19:40
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    final String[] notLoginInterceptPaths = {"/login/**"};

    //默认首页
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("doc.html");
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }
    //静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/web/**").addResourceLocations("classpath:/web/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       // registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(notLoginInterceptPaths);

        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new InterceptorConfig());
        registration.addPathPatterns("/**");                      //所有路径都被拦截
        registration.excludePathPatterns(                         //添加不拦截路径
                "你的登陆路径",            //登录
                "/*/*.html",            //html静态资源
                "/*/*.js",              //js静态资源
                "/*/*.css",             //css静态资源
                "/*/*.woff",
                "/*/*.png",   //图片
                "/*/*.ttf"
        );
    }
}

