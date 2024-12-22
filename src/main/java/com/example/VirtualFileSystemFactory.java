package com.example;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class VirtualFileSystemFactory implements FileSystemFactory {
	
	@Autowired
	private InMemoryFileSystem fileSystem;
	private final ConcurrentHashMap<Path, FileMetadata> metadataMap = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		initializeVirtualFS();
	}

	@Override
	public FileSystem createFileSystem(SessionContext session) throws IOException {
		Path root = Paths.get(System.getProperty("user.home"), "virtual-sftp");
		root.toFile().mkdirs();
		return new InMemoryFileSystemProvider(root, fileSystem).newFileSystem(root, null);
	}

	private void initializeVirtualFS() {
		
		createDirectoryWithMetadata("/documents", "owner1", "group1");
		createDirectoriesWithMetadata("/documents/Test/Hello/01/001", "owner2", "group2");
		createDirectoriesWithMetadata("/documents/Test/Hello/01/002", "owner3", "group3");
		createDirectoriesWithMetadata("/documents/Test/Hello/02/001", "owner4", "group4");
		createDirectoryWithMetadata("/images", "owner5", "group5");
		createDirectoryWithMetadata("/logs", "owner6", "group6");
		createFileWithMetadata("/documents/readme.txt", "Welcome to virtual SFTP!\n".getBytes(StandardCharsets.UTF_8),
				"owner7", "group7");
		createFileWithMetadata("/logs/server.log", "Server started at 2024-01-01\n".getBytes(StandardCharsets.UTF_8),
				"owner8", "group8");
	}

	private void createDirectoryWithMetadata(String path, String owner, String group) {
		fileSystem.createDirectory(path);
		metadataMap.put(Paths.get(path), new FileMetadata(owner, group));
	}
	
	private void createDirectoriesWithMetadata(String path, String owner, String group) {
		fileSystem.createDirectories(path);
		metadataMap.put(Paths.get(path), new FileMetadata(owner, group));
	}

	private void createFileWithMetadata(String path, byte[] content, String owner, String group) {
		fileSystem.createFile(path, content);
		metadataMap.put(Paths.get(path), new FileMetadata(owner, group));
	}

	public FileMetadata getFileMetadata(Path path) {
		return metadataMap.get(path);
	}

	@Override
	public Path getUserHomeDir(SessionContext session) throws IOException {
		return Paths.get(System.getProperty("user.home"), "virtual-sftp");
	}
}