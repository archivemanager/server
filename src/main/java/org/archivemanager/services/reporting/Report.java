package org.archivemanager.services.reporting;

import org.archivemanager.data.Record;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.util.StringFormatUtility;


public abstract class Report {
	private String name;
	private String label;
		
	
	public Report(String name, String label) {
		this.name = name;
		this.label = label;
	}
	
	public RestResponse<Record> generate(SearchRequest request) {
		return new RestResponse<Record>();
	}	
	
	protected String format(String in) {
		if(in == null || in.length() == 0) return "";
		QName qname = new QName(in);
		return StringFormatUtility.toTitleCase(qname.getLocalName().replace("_", " "));
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
}
