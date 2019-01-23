package org.archivemanager.models.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.archivemanager.models.system.QName;
import org.archivemanager.util.StringFormatUtility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "localName" }, allowGetters=true)
public class ModelRelation implements ModelObject {
	private static final long serialVersionUID = -1070736902043902312L;
	private final static Logger log = Logger.getLogger(ModelRelation.class.getName());
	private Long id;
	private String uid;
	private QName qname;
	private QName mapping;
	private String description;
	private String label;
	private String type;
	private String view;
	private QName startName;
	private QName endName;
	private int direction;
	private boolean many;
	private boolean cascade;
	private boolean hidden;
	private List<ModelField> fields = new ArrayList<ModelField>();
	private Model model;
	
	public static final int DIRECTION_OUTGOING = 1;
	public static final int DIRECTION_INCOMING = 2;
	
	public ModelRelation(){}
	public ModelRelation(Long id, QName qname) {
		this.id = id;
    	this.qname = qname;
	}
	public ModelRelation(Long id, QName startNode, QName endNode, int direction, QName qname) {
		this.id = id;
		this.startName = startNode;
		this.endName = endNode;
		this.direction = direction;
    	if(qname != null) {
    		this.qname = qname;
    	}
	}
	public ModelRelation(QName qname, String label, String description) {
		this.qname = qname;
		this.label = label;
		this.description = description;
	}	
	
	public String getName() {
		if(label == null || label.length() == 0) return StringFormatUtility.toTitleCase(qname.getLocalName());
		return label;
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
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public boolean isMany() {
		return many;
	}
	public void setMany(boolean many) {
		this.many = many;
	}
	public boolean isCascade() {
		return cascade;
	}
	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}	
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public List<ModelField> getFields() {
		return fields;
	}
	public void setFields(List<ModelField> fields) {
		this.fields = fields;
	}
	public ModelField getField(QName qname) {
		for(ModelField field : fields) {
			if(field.getQName().equals(qname))
				return field;
		}
		Model parentModel = model;
		while(parentModel != null) {	
			for(ModelField field : parentModel.getFields()) {
				if(field.getQName().equals(qname))
					return field;
			}
			parentModel = parentModel.getParent();
		}
		log.severe("field not found : "+this.qname+", "+qname.toString());
		return null;
	}
	public QName getStartName() {
		return startName;
	}
	public void setStartName(QName startName) {
		this.startName = startName;
	}
	public QName getEndName() {
		return endName;
	}
	public void setEndName(QName endName) {
		this.endName = endName;
	}
	@JsonIgnore
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public QName getMapping() {
		return mapping;
	}
	public void setMapping(QName mapping) {
		this.mapping = mapping;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
		
}