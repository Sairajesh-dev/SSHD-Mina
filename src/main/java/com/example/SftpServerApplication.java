package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class SftpServerApplication {
    @Autowired
    private SftpServer sftpServer;

    public static void main(String[] args) {
        SpringApplication.run(SftpServerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startSftpServer() throws InterruptedException {
        sftpServer.start();
        
        // Keep the application running
        Thread.currentThread().join();
    }
} 