package org.archivemanager.services.content;

import java.util.ArrayList;
import java.util.List;


public class DirectoryNode {
	private String uid;
	private String name;
	private String path;
	private List<FileNode> files = new ArrayList<FileNode>();
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<FileNode> getFiles() {
		return files;
	}
	public void setFiles(List<FileNode> files) {
		this.files = files;
	}
	
}
