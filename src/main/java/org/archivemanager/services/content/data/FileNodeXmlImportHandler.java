package org.archivemanager.services.content.data;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.content.FileNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FileNodeXmlImportHandler extends DefaultHandler {
	StringBuffer buff = new StringBuffer();
	List<FileNode> nodes = new ArrayList<FileNode>();
	FileNode currentFile = null;
	
	
	public List<FileNode> getNodes() {
		return nodes;
	}
	
	public void startDocument() throws SAXException {
		nodes.clear();
		super.startDocument();
	}
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
		if(qName.equals("node")) {
			String uid = attrs.getValue("uid");
			currentFile = new FileNode(uid, uid, null);		
		}
		/*
		if(qName.equals("dir")) {
			String file_uid = attrs.getValue("uid");
			String path = attrs.getValue("path");
			FileNode newNode = new FileNode(datasource.getId(), file_uid, stack.peek().getUid(), "dir", path);
			stack.push(newNode);
			currentType = "dir";
		} else if(qName.equals("file")) {
			String file_uid = java.util.UUID.randomUUID().toString();
			String path = attrs.getValue("path");
			String size = attrs.getValue("size");
			FileNode newNode = new FileNode(datasource.getId(), file_uid, stack.peek().getUid(), "file", path);
			stack.push(newNode);
			if(size != null && size.length() > 0) newNode.setSize(Long.valueOf(size));
			currentFile = newNode;
			currentType = "file";
		}
		*/
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		buff.append(ch,start,length);
	}
	
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException	{
		String data = buff.toString().trim();		
		if(qName.equals("name")) {
			currentFile.setName(data);
		} else if(qName.equals("path")) {
			currentFile.setPath(data);
		} else if(qName.equals("children")) {
			currentFile.setChildren(Boolean.parseBoolean(data));
		} else if(qName.equals("node")) {
			nodes.add(currentFile);
		}
		buff = new StringBuffer();
		
	}
	
	@Override
	public void endDocument() throws SAXException {
		/*
		for(FileNode child : datasource.getNodes().values()) {
			FileNode parent = datasource.getNodes().get(child.getParent());
			if(parent != null) {
				parent.getChildren().add(child);
			} else {
				datasource.getRoot().getChildren().add(child);
			}
		}
		*/
	}
}
