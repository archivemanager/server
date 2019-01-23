package org.archivemanager.data;

import java.util.ArrayList;
import java.util.List;


public class LinkedTreeNode {
	private String code;
	private String name;
	private LinkedTreeNode parent;
	private List<LinkedTreeNode> children = new ArrayList<LinkedTreeNode>();
		
	public LinkedTreeNode(String code, String name) {
		this.code = code;
		this.name = name;
	}
				
	public void setCode(String code) {
		this.code = code;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setParent(LinkedTreeNode parent) {
		this.parent = parent;
	}
	public void setChildren(List<LinkedTreeNode> children) {
		this.children = children;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public LinkedTreeNode getParent() {
		return parent;
	}
	public List<LinkedTreeNode> getChildren() {
		return children;
	}
}
