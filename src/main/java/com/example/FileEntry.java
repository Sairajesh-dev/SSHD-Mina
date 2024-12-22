package com.example;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class FileEntry {
	private byte[] content;
	private boolean isDirectory;
	private long lastModified;
	private long size;

	public FileEntry() {
		this.path = "";
	} // Default constructor for Spring

	public FileEntry(byte[] content, boolean isDirectory) {
		this.path = "";
		this.content = content;
		this.isDirectory = isDirectory;
		this.lastModified = Instant.now().toEpochMilli();
		this.size = content != null ? content.length : 0;
	}

	// Getters and setters
	public byte[] getContent() {
		return content;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public long getLastModified() {
		return lastModified;
	}

	public long getSize() {
		return size;
	}

	private final String path;
	private String owner = "defaultOwner";
	private String group = "defaultGroup";

	public FileEntry(byte[] content, boolean isDirectory, long lastModified, long size, String path, String owner,
			String group) {
		super();
		this.content = content;
		this.isDirectory = isDirectory;
		this.lastModified = lastModified;
		this.size = size;
		this.path = path;
		this.owner = owner;
		this.group = group;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}