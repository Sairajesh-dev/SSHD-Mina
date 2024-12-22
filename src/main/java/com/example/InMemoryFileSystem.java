package com.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class InMemoryFileSystem {
	private final ConcurrentHashMap<String, FileEntry> files = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		// Initialize root directory
		createDirectory("/");
	}

	public void createDirectory(String path) {
		files.put(path, new FileEntry(null, true));
	}

	public void createFile(String path, byte[] content) {
		files.put(path, new FileEntry(content, false));
	}

	public FileEntry getEntry(String path) {
		return files.get(path);
	}

	public boolean exists(String path) {
		return files.containsKey(path);
	}

	public void delete(String path) {
		files.remove(path);
	}
	
	public FileEntry getFile(String path) {
        return files.get(path);
    }

    public void setOwner(String path, String owner) {
    	FileEntry file = files.get(path);
        if (file != null) {
            file.setOwner(owner);
        }
    }

    public void setGroup(String path, String group) {
    	FileEntry file = files.get(path);
        if (file != null) {
            file.setGroup(group);
        }
    }


	public void createDirectories(String path) {

		String dirNames[] = path.split("/");
		String dirNamePath = "";

		for (String dirName : dirNames) {
			if (dirName != null && !dirName.equals("")) {
				files.put(path, new FileEntry(null, true));
				dirName = "/" + dirName;
				dirNamePath = dirNamePath + dirName;
				files.put(dirNamePath, new FileEntry(null, true));
			}

		}

	}

	public Set<String> getDirectoryContents(String path) {
		Set<String> contents = new HashSet<>();
		String prefix = path.endsWith("/") ? path : path + "/";

		files.forEach((filePath, entry) -> {
			if (filePath.startsWith(prefix)) {
				String relativePath = filePath.substring(prefix.length());
				if (!relativePath.isEmpty() && !relativePath.contains("/")) {
					contents.add(relativePath);
				}
			}
		});

		return contents;
	}

}