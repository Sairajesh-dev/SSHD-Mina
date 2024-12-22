package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileSystemConfig {
    
    @Bean(name = "rootPath")
    public Path rootPath() {
        // You can customize this path as needed
        return Paths.get(System.getProperty("user.home"), "sftp-root");
    }
} 