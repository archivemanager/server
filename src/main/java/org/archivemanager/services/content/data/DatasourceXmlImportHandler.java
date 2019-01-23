package org.archivemanager.services.content.data;
import java.util.Stack;

import org.archivemanager.services.content.Datasource;
import org.archivemanager.services.content.FileNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DatasourceXmlImportHandler extends DefaultHandler {
	private Datasource datasource;
	StringBuffer buff = new StringBuffer();
	Stack<FileNode> stack = new Stack<FileNode>();
	FileNode currentFile = null;
	String currentType = "";
	
	public DatasourceXmlImportHandler(Datasource datasource) {
		this.datasource = datasource;
	}
	
	public void startDocument() throws SAXException {
		super.startDocument();
		stack.push(datasource.getRoot());
	}
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
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
		/*
		String data = buff.toString().trim();
		if(qName.equals("name")) {
			if(currentType.equals("dir"))
				stack.peek().setName(data);
			else
				currentFile.setName(data);
		} else if(qName.equals("dir") || qName.equals("file")) {
			FileNode node = stack.pop();
			datasource.getNodes().put(node.getUid(), node);
		}
		buff = new StringBuffer();
		*/
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
