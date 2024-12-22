package com.example;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualFile {
    private static final ConcurrentHashMap<String, byte[]> fileContents = new ConcurrentHashMap<>();
    
    public static void writeContent(Path path, byte[] content) {
        fileContents.put(path.toString(), content);
    }
    
    public static byte[] readContent(Path path) {
        return fileContents.getOrDefault(path.toString(), new byte[0]);
    }
} 