package org.archivemanager.web.application;

import java.io.StringWriter;
import java.util.List;

import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.Role;
import org.archivemanager.web.model.FolderTreeNode;
import org.archivemanager.web.model.TreeNode;

public abstract class ApplicationController {

	
	protected String getRolesString(List<Role> roles) {
		StringWriter writer = new StringWriter();
		writer.append("[");
		for(int i=0; i < roles.size(); i++) {
			writer.append("'"+roles.get(i).getName()+"'");
			if(i < roles.size() - 1) writer.append(",");
		}
		writer.append("]");
		return writer.toString().trim();
	}
	protected FolderTreeNode createFolderTreeNode(QName qname, long id, String name, String state){
		FolderTreeNode node = new FolderTreeNode(id, qname.toString(), name, state);
		String iconCls = "icon-" + qname.getLocalName();
		node.setIconCls(iconCls);
		return node;
	}
	protected TreeNode createTreeNode(QName qname, long id, String name, String state){
		TreeNode node = new TreeNode(id, qname.toString(), "", name, state);
		String iconCls = "icon-" + qname.getLocalName();
		node.setIconCls(iconCls);
		return node;
	}
	protected TreeNode createTreeNode(QName qname, long id, String type, String iconCls, String name, String state){
		TreeNode node = new TreeNode(id, qname.toString(), type, name, state);
		node.setIconCls(iconCls);
		return node;
	}
}
