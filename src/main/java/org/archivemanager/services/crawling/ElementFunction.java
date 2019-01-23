package org.archivemanager.services.crawling;

import java.util.ArrayList;
import java.util.List;

public class ElementFunction {
	private String type;
	private String value;
	private List<ElementValue> values = new ArrayList<ElementValue>();
	
	
	public ElementFunction(){}
	public ElementFunction(String type) {
		this.type = type;
	}
	public ElementFunction(String type,String value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<ElementValue> getValues() {
		return values;
	}
	public void setValues(List<ElementValue> values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		if(values.size() > 0) {
			String strValue = type+"(";
			for(ElementValue val : values) {
				strValue = strValue + val.getValue() + ", ";
			}
			return strValue.substring(0, strValue.length()-2) + ")";
		}
		return type+"("+value+")";
	}
}
