package org.archivemanager.data;

import java.io.StringWriter;

public class RequestBuffer extends StringWriter {
	
	
	public boolean lastChar(char c) {
		if(getBuffer().length() == 0) return false;
		return getBuffer().charAt(length()-1) == c;
	}
	public int length() {
		return getBuffer().length();
	}
	public void setLength(int length) {
		getBuffer().setLength(length);
	}
}
