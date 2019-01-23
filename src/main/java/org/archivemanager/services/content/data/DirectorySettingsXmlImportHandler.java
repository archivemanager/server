package org.archivemanager.services.content.data;

import org.archivemanager.services.content.DirectorySettings;
import org.archivemanager.services.content.FileSettings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class DirectorySettingsXmlImportHandler extends DefaultHandler {
	StringBuffer buff = new StringBuffer();
	DirectorySettings settings;
	FileSettings file;
	boolean inFile = false;
	
	public DirectorySettings getDirectorySettings() {
		return settings;
	}
	
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
		if(qName.equals("directory")) {
			settings = new DirectorySettings();
			String uid = attrs.getValue("uid");
			settings.setUid(uid);		
		} else if(qName.equals("file")) {
			inFile = true;
			file = new FileSettings();
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
			else settings.setName(data);
		} else if(qName.equals("path")) {
			if(inFile) file.setPath(data);
			else settings.setPath(data);
		} else if(qName.equals("file")) {
			settings.getFiles().add(file);
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
