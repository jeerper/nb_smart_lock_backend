package com.summit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FaceRecognitionWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    FaceRecognitionInterceptor faceRecognitionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(faceRecognitionInterceptor)
                .addPathPatterns("/**/face-recognition/**")
                .excludePathPatterns("/**/face-recognition/face-scan/**");

    }
}


