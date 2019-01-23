package org.archivemanager.models.dictionary;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;
import org.archivemanager.util.StringFormatUtility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "localName" }, allowGetters=true)
public class ModelField implements ModelObject,Cloneable {
	private static final long serialVersionUID = 4234751793033801218L;
	private long id;
	private String uid;
	private Model model;
	private ModelRelation relation;
	private QName qname;
	private String description;
	private String label;
	private String value;
	private String type;
	private String format = "";
	private int index;
	private boolean mandatory = false;
	private boolean unique = false;
	private boolean hidden = false;	
	private boolean sortable = false;
	private boolean searchable = false;
	private int minValue;
	private int maxValue;
	private int minSize;
	private int maxSize;
	private int order;	
	private int sort;
	private String defaultValue;
	private List<ModelFieldValue> values = new ArrayList<ModelFieldValue>();
	private List<ModelFieldAspect> aspects = new ArrayList<ModelFieldAspect>();
	
	public static final String TYPE_NULL = "null";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_DOUBLE = "double";    
    public static final String TYPE_DATE = "date";
    public static final String TYPE_LONGTEXT = "longtext";
    public static final String TYPE_MEDIUMTEXT = "mediumtext";
    public static final String TYPE_SMALLTEXT = "smalltext";
    public static final String TYPE_SERIALIZABLE = "serializable";
    public static final String TYPE_ASPECT = "aspect";
    public static final String TYPE_VALUES = "values";
    public static final String TYPE_COMPUTED = "computed";
    
    public static final String FORMAT_PASSWORD = "password";
    public static final String FORMAT_EMAIL = "email";
    public static final String FORMAT_HTML = "html";
    public static final String FORMAT_URL = "url";
    public static final String FORMAT_CURRENCY = "currency";
    public static final String FORMAT_RICHTEXT = "richtext";
    
    public ModelField(){}
    public ModelField(long id, QName qname) {
    	this.id = id;
    	this.qname = qname;
	}
    public ModelField(String type, String label, QName qname) {
    	this.type = type;
    	this.label = label;
    	this.qname = qname;
	}
    public ModelField(QName qname, String label, String description) {
		this.qname = qname;
		this.label = label;
		this.description = description;
	}	
    public ModelField(Long id, QName qname, String label, String description) {
    	this.id = id;
		this.qname = qname;
		this.label = label;
		this.description = description;
	}
    
    public String getName() {
		if(label == null || label.length() == 0) return StringFormatUtility.toTitleCase(qname.getLocalName());
		return label;
	}
    public String getLocalName() {
		return qname.getLocalName();
	}
    public ModelFieldValue getValue(String value) {
    	for(ModelFieldValue modelValue : values) {
    		if(modelValue.getValue().equals(value))
    			return modelValue;
    	}
    	return null;
    }
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}	
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public boolean isUnique() {
		return unique;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}		
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getMinSize() {
		return minSize;
	}
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		if(format != null) this.format = format;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public List<ModelFieldValue> getValues() {
		return values;
	}
	public void setValues(List<ModelFieldValue> values) {
		this.values = values;
	}
	public List<ModelFieldAspect> getAspects() {
		return aspects;
	}
	public void setAspects(List<ModelFieldAspect> aspects) {
		this.aspects = aspects;
	}
	@JsonIgnore
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	@JsonIgnore
	public ModelRelation getRelation() {
		return relation;
	}
	public void setRelation(ModelRelation relation) {
		this.relation = relation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
