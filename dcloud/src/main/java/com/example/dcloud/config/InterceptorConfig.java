package com.example.dcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
<<<<<<< Updated upstream
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**"); // 拦截所有请求，通过判断是否有 @LoginRequired 注解
=======
      //  registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**"); // 拦截所有请求，通过判断是否有 @NoToken 注解
>>>>>>> Stashed changes
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}

