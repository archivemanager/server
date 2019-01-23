package org.archivemanager.services.crawling;

import java.util.ArrayList;
import java.util.List;

public class ElementSelector {
	private String type;
	private String stage = STAGE_EXTRACT;
	private String value;
	private String attribute;
	private String format;
	private int count;
	private boolean own;
	private List<ElementFunction> functions = new ArrayList<ElementFunction>();
	
	public static final String TYPE_PAGING = "paging";
	public static final String TYPE_TOTAL = "total";
	public static final String TYPE_SEED = "seed";
	public static final String TYPE_BODY = "body";
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_URL = "url";
	public static final String TYPE_NAME = "name";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_TIME = "time";
	public static final String TYPE_SUMMARY = "summary";
	public static final String TYPE_ATTRIBUTE = "attribute";
	
	public static final String STAGE_SEED = "seed";
	public static final String STAGE_EXTRACT = "extract";
	
	public ElementSelector(){}
	public ElementSelector(String type) {
		this.type = type;
	}
	public ElementSelector(String type, String value) {
		this.type = type;
		this.value = value;
	}
	public ElementSelector(String type, String stage, String value) {
		this.type = type;
		this.stage = stage;
		this.value = value;
	}
	public ElementSelector(String type, String stage, String value, String format) {
		this.type = type;
		this.stage = stage;
		this.value = value;
		this.format = format;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public List<ElementFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<ElementFunction> functions) {
		this.functions = functions;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean getOwnText() {
		return own;
	}
	public void setOwnText(boolean own) {
		this.own = own;
	}
	
}
