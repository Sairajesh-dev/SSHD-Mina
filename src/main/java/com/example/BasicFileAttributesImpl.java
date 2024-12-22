package com.example;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.springframework.stereotype.Component;

@Component
public class BasicFileAttributesImpl implements BasicFileAttributes {
    private final FileEntry entry;

    public BasicFileAttributesImpl(FileEntry entry) {
        this.entry = entry;
    }

    @Override
    public FileTime lastModifiedTime() { return FileTime.fromMillis(entry.getLastModified()); }
    @Override
    public FileTime lastAccessTime() { return FileTime.fromMillis(entry.getLastModified()); }
    @Override
    public FileTime creationTime() { return FileTime.fromMillis(entry.getLastModified()); }
    @Override
    public boolean isRegularFile() { return !entry.isDirectory(); }
    @Override
    public boolean isDirectory() { return entry.isDirectory(); }
    @Override
    public boolean isSymbolicLink() { return false; }
    @Override
    public boolean isOther() { return false; }
    @Override
    public long size() { return entry.getSize(); }
    @Override
    public Object fileKey() { return null; }
    
} 