package com.example;

public class FileMetadata {
	
	private String owner;
	private String group;

	public FileMetadata(String owner, String group) {
		this.owner = owner;
		this.group = group;
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
