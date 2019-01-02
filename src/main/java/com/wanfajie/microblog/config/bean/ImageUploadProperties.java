package com.wanfajie.microblog.config.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

@Component
@PropertySource("classpath:/application.properties")
@ConfigurationProperties(
    prefix = "spring.upload.image"
)
public class ImageUploadProperties {
    private String path;
    private Set<String> allowSuffix;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getAllowSuffix() {
        return allowSuffix;
    }

    public void setAllowSuffix(Set<String> allowSuffix) {
        this.allowSuffix = allowSuffix;
    }

    public String getFullPath() {
        return getFullPathFile()
                .getAbsolutePath();
    }

    public File getFullPathFile() {

        String fullPath = path;
        if (path.startsWith("classpath:")) {
            String basePath = this.getClass().getResource("/").getPath();
            fullPath = path = basePath + path.substring(10);
        }

        return new File(fullPath);
    }
}
