package org.archivemanager.services.content;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class FileNode implements Serializable {
	private static final long serialVersionUID = 4003623476900051814L;
	private final static Logger log = Logger.getLogger(FileNode.class.getName());
	private String datasource;
	private String id;
	private String uid;
	private String fid;
	private String name;
	private String path;
	//private File file;
	private String contentType;
	private int width;
	private int height;
	private long size;
	private boolean children;
	private List<FileNode> files = new ArrayList<FileNode>();
	
	public FileNode(){}
	public FileNode(String datasource, String uid, String path) {
		this.datasource = datasource;
		this.uid = uid;
		this.path = path;
		if(datasource == null) 
			log.info("FileNode with null datasource created");
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
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
	public boolean hasChildren() {
		return children;
	}
	public void setChildren(boolean children) {
		this.children = children;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
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
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
