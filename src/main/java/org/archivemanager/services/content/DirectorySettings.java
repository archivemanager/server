package org.archivemanager.services.content;
import java.util.ArrayList;
import java.util.List;


public class DirectorySettings {
	private String uid;
	private String name;
	private String path;
	private List<FileSettings> files = new ArrayList<FileSettings>();
	
	
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
	public List<FileSettings> getFiles() {
		return files;
	}
	public void setFiles(List<FileSettings> files) {
		this.files = files;
	}
	
}
