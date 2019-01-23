package org.archivemanager.web.model;

import java.util.ArrayList;
import java.util.List;


public class FolderTreeNode extends TreeNode {
	private List<TreeNode> children = new ArrayList<TreeNode>();
	
	public FolderTreeNode(long id, String qname, String text, String state) {
		super(id,qname,"", text,state);
	}
		
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
}
