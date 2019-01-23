/*
 * Copyright (C) 2009 Heed Technology Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.archivemanager.util;

import java.util.Date;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

import org.archivemanager.data.io.CharSpan;


public class StringUtility {
	private static CollapsedStringAdapter whiteSpaceStripper = new CollapsedStringAdapter();
	
		
	public static String cleanUpWhiteSpace(String stringToStrip) {
		return whiteSpaceStripper.unmarshal(stringToStrip);
	}
	public static boolean isNotEmpty(String val) {
		if(val != null && val.length() > 0)
			return true;
		else
			return false;
	}
	public static String removeTags(String in) {
		if(in == null) return "";
    	String noHTMLString = in.replaceAll("\\<.*?>","");
	    return noHTMLString;
	}
	public static Class<?> getJavaClass(String in) {
		if(in.equals("java.lang.String")) return String.class;
		if(in.equals("java.lang.Integer")) return Integer.class;
		if(in.equals("java.lang.Long")) return Long.class;
		if(in.equals("java.lang.Float")) return Float.class;
		if(in.equals("java.lang.Double")) return Double.class;
		if(in.equals("java.util.Date")) return Date.class;
		if(in.equals("java.lang.Boolean")) return Boolean.class;
		return null;
	}
	public static boolean equals(CharSpan span, String str) {
		if (span.size() != str.length()) {
			return false;
		}
		int size = span.size();

		char [] arr = span.getArray();
		int offset = span.getOffset();
		
		for (int i = 0; i < size; i++) {
			if (!(arr[offset + i] == str.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	public static boolean equals(CharSpan span0, CharSpan span1) {
		if (span0.size() != span1.size()) {
			return false;
		}
		int size = span0.size();

		char [] arr0 = span0.getArray();
		char [] arr1 = span1.getArray();
		
		int offset0 = span0.getOffset();
		int offset1 = span1.getOffset();
		
		for (int i = 0; i < size; i++) {
			if (arr0[offset0++] != arr1[offset1++]) {
				return false;
			}
		}
		
		return true;
	}
	public static int hashCodeForCharSpan(CharSpan span) {
		char [] arr = span.getArray();
		int start = span.getOffset();
		int end = start + span.size();
        int result = 1;
		for (int i = start; i < end; i++) {
            result = 31 * result + arr[i];
		}
        return result;
	}
	public static String luceneEscape(String in) {
		String escapeChars ="[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
		String escaped = in.replaceAll(escapeChars, "\\\\$0");
		return escaped;
	}
	public static String toTitleCase(String givenString) {
		if(givenString == null) return "";
	    String[] arr = givenString.split(" ");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < arr.length; i++) {
	        sb.append(Character.toUpperCase(arr[i].charAt(0)))
	            .append(arr[i].substring(1)).append(" ");
	    }          
	    return sb.toString().trim();
	}  
}
