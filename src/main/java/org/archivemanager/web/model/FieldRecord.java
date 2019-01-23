package org.archivemanager.web.model;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.system.QName;


public class FieldRecord {
	private QName qname;
	private String label;
	private String type;
	private String format;
	private int minValue;
	private int maxValue;
	private boolean searchable;
	private List<ValueRecord> values = new ArrayList<ValueRecord>();
	
	
	public FieldRecord(ModelField field) {
		this.qname = field.getQName();
		this.label = field.getLabel();
		this.type = field.getType();
		this.format = field.getFormat();
		this.minValue = field.getMinValue();
		this.maxValue = field.getMaxValue();
		this.searchable = field.isSearchable();
		for(ModelFieldValue value : field.getValues()) {
			ValueRecord record = new ValueRecord(value);
			values.add(record);
		}
	}
	
	public String getLocalName() {
		return qname.getLocalName();
	}
	
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}	
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public List<ValueRecord> getValues() {
		return values;
	}
	public void setValues(List<ValueRecord> values) {
		this.values = values;
	}
	
}
