package org.archivemanager.data.io;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.archivemanager.services.net.http.HttpComponent;
import org.archivemanager.services.net.http.HttpResponse;
import org.archivemanager.services.net.http.URLConnectionComponent;
import org.archivemanager.util.XMLUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RSSParser {
	private HttpComponent http = new URLConnectionComponent();
	private List<RSSRecord> data = new ArrayList<RSSRecord>();
	
	
	public RSSParser() {
		// TODO Auto-generated constructor stub
	}	
	
	public void process(String url) throws Exception {
		data.clear();
		HttpResponse response = null;
		try {
			HashMap<String,String> headers = new HashMap<String,String>();
			response = http.get(url, headers, null);
		} catch(Exception e) {
			System.out.println("problem fetching : "+url);
		}
		if(response != null && response.getContent() != null) {
			InputStream stream = new ByteArrayInputStream(response.getContentBytes());
			XMLUtility.SAXParse(false, stream, new ImportHandler());
		}		
	}
	public void process(InputStream stream) throws Exception {
		data.clear();
		XMLUtility.SAXParse(false, stream, new ImportHandler());
	}
	
	public List<RSSRecord> getData() {
		return data;
	}
	public void setData(List<RSSRecord> data) {
		this.data = data;
	}

	public class ImportHandler extends DefaultHandler {
		StringBuffer buffer = null;
		RSSRecord record = null;
		
		public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
			if(qName.equals("item")) buffer = new StringBuffer();
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			buffer.append(ch,start,length);
		}
		public void endElement(String namespaceURI, String sName, String qName) throws SAXException	{
			if(qName.equals("item")) {
				data.add(record);
			} else if(qName.equals("title")) {
				record.setTitle(buffer.toString().trim());
			} else if(qName.equals("link")) {
				record.setLink(buffer.toString().trim());
			} else if(qName.equals("description")) {
				record.setDescription(buffer.toString().trim());
			} else if(qName.equals("pubDate")) {
				record.setPublishDate(buffer.toString().trim());
			}
		}
	}
}
