package com.example;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SftpServer {
    private static final int PORT = 2222;
    private SshServer sshd;
    
    @Value("${sftp.username:user}")
    private String username;
    
    @Value("${sftp.password:password}")
    private String password;

    @Autowired
    private VirtualFileSystemFactory virtualFileSystemFactory;

    public void start() {
        try {
            sshd = SshServer.setUpDefaultServer();
            System.out.println("Configuring SFTP server...");
            sshd.setPort(PORT);
            sshd.setHost("0.0.0.0");
            
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
            
            sshd.setPasswordAuthenticator((username, password, session) -> 
                this.username.equals(username) && this.password.equals(password));
            
            SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();
            sshd.setSubsystemFactories(Collections.singletonList(factory));
            
//            SftpSubsystem sftpSubSys = new SftpSubsystem(null, factory);
            
            sshd.setFileSystemFactory(virtualFileSystemFactory);

            sshd.start();
            System.out.println("SFTP server started successfully");
            System.out.println("Listening on 0.0.0.0:" + PORT);
            
        } catch (IOException e) {
            System.err.println("Failed to start SFTP server: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 