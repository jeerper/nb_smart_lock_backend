package com.summit.config;

import cn.hutool.system.SystemUtil;
import com.summit.MainAction;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FileAccessWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + MainAction.SnapshotFileName + "/**").addResourceLocations("file:" + SystemUtil.getUserInfo().getCurrentDir() + File.separator + MainAction.SnapshotFileName + File.separator);
    }


}


