package org.archivemanager.services.content.data;

import org.archivemanager.services.content.DirectoryNode;
import org.archivemanager.services.content.FileNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class DirectoryNodeXmlImportHandler extends DefaultHandler {
	StringBuffer buff = new StringBuffer();
	DirectoryNode directory;
	FileNode file;
	boolean inFile = false;
	
	public DirectoryNode getDirectoryNode() {
		return directory;
	}
	
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
		if(qName.equals("node")) {
			directory = new DirectoryNode();
			String uid = attrs.getValue("uid");
			directory.setUid(uid);		
		} else if(qName.equals("file")) {
			inFile = true;
			file = new FileNode();
			String uid = attrs.getValue("uid");
			file.setUid(uid);
			String size = attrs.getValue("size");
			if(size != null && size.length() > 0) file.setSize(Long.valueOf(size));
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		buff.append(ch,start,length);
	}
	
	public void endElement(String namespaceURI, String sName, String qName) throws SAXException	{
		String data = buff.toString().trim();		
		if(qName.equals("name")) {
			if(inFile) file.setName(data);
			else directory.setName(data);
		} else if(qName.equals("path")) {
			if(inFile) file.setPath(data);
			else directory.setPath(data);
		} else if(qName.equals("file")) {
			directory.getFiles().add(file);
			inFile = false;
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
