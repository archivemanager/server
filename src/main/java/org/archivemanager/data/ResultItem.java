package org.archivemanager.data;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;

public class ResultItem implements Result {
	private String id;
	private String uid;
	private QName qname;
	private List<QName> fields = new ArrayList<QName>();
	private List<String> values = new ArrayList<String>();
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public String getValue(QName name) {
		for(QName qname : fields) {
			if(qname.equals(name)) {
				int index = fields.indexOf(qname);
				return values.get(index);
			}
		}
		return null;
	}
	public void addValue(QName name, String value) {
		fields.add(name);
		values.add(value);
	}
	public List<QName> getFields() {
		return fields;
	}
	public void setFields(List<QName> fields) {
		this.fields = fields;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
}
