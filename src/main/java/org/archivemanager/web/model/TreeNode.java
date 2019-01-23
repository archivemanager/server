package org.archivemanager.web.model;



public class TreeNode {
	private long id;
	private String qname;
	private String type;
	private String text;
	private String state;
	private String iconCls;
	
	
	public TreeNode(long id, String qname, String type, String text, String state) {
		this.id = id;
		this.qname = qname;
		this.type = type;
		this.text = text;
		this.state = state;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}	
	public String getQName() {
		return qname;
	}
	public void setQName(String qname) {
		this.qname = qname;
	}	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}	
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String icon) {
		this.iconCls = icon;
	}
	
}
