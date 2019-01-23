/*
 * Copyright (C) 2011 Heed Technology Inc.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.archivemanager.services.net.http.HttpRequest;


public class HttpUtility {
	private final static Logger log = Logger.getLogger(HttpUtility.class.getName());
	
	public static String getLastPathName(HttpRequest req) {
		return req.getUrl().substring(req.getUrl().lastIndexOf("/")+1);
	}
		
	protected static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else { 
			log.info("stream is empty returning ''.");
			return "";
		}
	}
	
	public static Double getParmDoub(HttpRequest req, String parm) {
		String value = req.getParameter(parm);
		if(NumberUtility.isDouble(value))
			return Double.valueOf(value);
		return 0.0;
	}
	public static Double getParmDoub(HttpRequest req, String parm, Double defaultVal) {
		String value = req.getParameter(parm);
		if(NumberUtility.isDouble(value))
			return Double.valueOf(value);
		return defaultVal;
	}
	public static Boolean getParmBool(HttpRequest req, String parm) {
		String value = req.getParameter(parm);
		if(value != null) {
			if(value.equals("true")) return Boolean.TRUE;
		}
		return false;
	}
	public static Boolean getParmBool(HttpRequest req, String parm, Boolean defaultVal) {
		String value = req.getParameter(parm);
		if(value != null) {
			if(value.equals("true")) return Boolean.TRUE;
		}
		return defaultVal;
	}
	public static String getParmStr(HttpRequest req, String parm) {
		String value = req.getParameter(parm);
		if(value != null)
			return value;
		return "";
	}
	public static String getParmStr(HttpRequest req, String parm, String defaultVal) {
		String value = req.getParameter(parm);
		if(value != null)
			return value;
		return defaultVal;
	}
	public static Integer getParmInt(HttpRequest req, String parm) {
		String value = req.getParameter(parm);
		if(NumberUtility.isInteger(value))
			return Integer.valueOf(value);
		return 0;
	}
	public static Integer getParmInt(HttpRequest req, String parm, Integer defaultVal) {
		String value = req.getParameter(parm);
		if(NumberUtility.isInteger(value))
			return Integer.valueOf(value);
		return defaultVal;
	}
	public static Long getParmLong(HttpRequest req, String parm) {
		String value = req.getParameter(parm);
		if(NumberUtility.isLong(value))
			return Long.valueOf(value);
		return 0L;
	}
	public static Long getParmLong(HttpRequest req, String parm, Long defaultVal) {
		String value = req.getParameter(parm);
		if(NumberUtility.isLong(value))
			return Long.valueOf(value);
		return defaultVal;
	}
}
