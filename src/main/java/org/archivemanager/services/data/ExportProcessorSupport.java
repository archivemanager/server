package org.archivemanager.services.data;

public class ExportProcessorSupport {

	
	protected String clean(Object in) {
		if(in == null) return "";
		return in.toString();
	}
	protected String csvClean(Object in) {
		if(in == null) return "";
		return in.toString().replaceAll("\\<.*?\\>", "").replace("\n", "");
	}
}
