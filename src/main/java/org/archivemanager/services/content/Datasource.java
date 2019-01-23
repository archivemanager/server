package org.archivemanager.services.content;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Datasource implements Serializable {
	private static final long serialVersionUID = -86560429285672359L;
	private long id;
	private String uid;
	private String type;
	private String name;
	private String url;
	private boolean children = true;
	private FileNode root;
	private Map<String, FileNode> nodes = new HashMap<String, FileNode>();
	
	
	public Datasource(long id, String uid, String type, String name, String url) {
		this.id = id;
		this.uid = uid;
		this.type = type;
		this.name = name;
		this.url = url;
		root = new FileNode(uid, uid, url);
	}
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public FileNode getRoot() {
		return root;
	}
	public void setRoot(FileNode root) {
		this.root = root;
	}
	public Map<String, FileNode> getNodes() {
		return nodes;
	}
	public void setNodes(Map<String, FileNode> nodes) {
		this.nodes = nodes;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isChildren() {
		return children;
	}
	public void setChildren(boolean children) {
		this.children = children;
	}
	
}
