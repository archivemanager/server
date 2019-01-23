package org.archivemanager.util;

public class HTMLUtility {

	public static String removeTags(String in) {
		if(in == null) return "";
    	String noHTMLString = in.replaceAll("\\<.*?>","");
    	return noHTMLString;
	}
	public static String escape(String in) {
		if(in != null) {
			in = in.replace("<", "&lt;");
			in = in.replace("&", "&amp;");
			in = in.replace("'", "&apos;");
			in = in.replace("\"", "&quot;");
			//in = in.replaceAll("%", "&#37;");
			return in.trim();
		}
		return "";
	}
	public static String unescape(String in) {
		if(in != null) {
			in = in.replace("%2520", " ");
			in = in.replace("%252C", ",");
			return in.trim();
		} 
		return "";
	}
}
