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
package org.archivemanager.services.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;


public class Entity implements Serializable {
	private static final long serialVersionUID = -5414261842849202782L;
	private Long id;
	private long xid;
    private QName qname;
    private String uid;
    private long created;
    private long modified;
    private long creator;
    private long modifier;
    private long accessed;
	private long user;
    private boolean deleted;
	private String source; //the id from third-party data provider
    private QName parentQName;
    private QName labelField = SystemModel.NAME;
    private float rawScore;
	private int normalizedScore;
	private List<QName> childQNames = new ArrayList<QName>(0);
    private List<ACE> entries = new ArrayList<ACE>(0);
    private List<QName> aspects = new ArrayList<QName>(0); 
    private List<Property> properties = new ArrayList<Property>(0);
    private List<Association> sourceAssociations = new ArrayList<Association>();
    private List<Association> targetAssociations = new ArrayList<Association>();
    
    public Entity() {}
    public Entity(QName qname) {
    	this.qname = qname;
    }
    public Entity(long id, QName qname) {
    	this.id = id;
    	this.qname = qname;
    }
     
    public String getName() {
    	Property property = getProperty(SystemModel.NAME);
    	if(property != null && property.getValue() != null)
    		return (String)property.getValue();
    	return null;
	}
    
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getLabel() {
    	if(hasProperty(labelField)) {
    		return getPropertyValueString(labelField);
    	}
    	return "";
    }
    public QName getLabelField() {
		return labelField;
	}
	public void setLabelField(QName labelField) {
		this.labelField = labelField;
	}
	public float getRawScore() {
		return rawScore;
	}
	public void setRawScore(float score) {
		this.rawScore = score;
	}
	public int getNormalizedScore() {
		return normalizedScore;
	}
	public void setNormalizedScore(int normalizedScore) {
		this.normalizedScore = normalizedScore;
	}
	public List<ACE> getEntries() {
		return entries;
	}
	public void setEntries(List<ACE> entries) {
		this.entries = entries;
	}
	public void addAspect(QName qname) {
    	aspects.add(qname);
    }    
    public List<QName> getAspects() {
        return this.aspects;
    }
    public void setAspects(List<QName> aspects) {
        this.aspects = aspects;
    }    
    public List<Property> getProperties() {
    	return properties.stream().collect(Collectors.toList());
    }
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getXid() {
		return xid;
	}
	public void setXid(Long xid) {
		this.xid = xid;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getModified() {
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}
	public long getCreator() {
		return creator;
	}
	public void setCreator(long creator) {
		this.creator = creator;
	}
	public long getModifier() {
		return modifier;
	}
	public void setModifier(long modifier) {
		this.modifier = modifier;
	}
	public long getAccessed() {
		return accessed;
	}
	public void setAccessed(long accessed) {
		this.accessed = accessed;
	}
	public long getUser() {
		return user;
	}
	public void setUser(long user) {
		this.user = user;
	}
	public Boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public void addProperty(Property property) {
    	if(property == null) return;
    	Property prop = getProperty(qname);
    	if(prop != null) {
    		prop.setValue(property.getValue());
    		return;
    	} else properties.add(property);
    }
	public void addProperty(QName qname, Object value) {
    	if(qname == null) return;
    	Property prop = getProperty(qname);
    	if(prop != null) {
    		prop.setValue(value);
    		return;
    	}
    	prop = new Property(qname, value);
    	if(prop != null) properties.add(prop);
    }
    public void addProperty(String type, QName qname, Object value) {
    	if(qname == null) return;
    	Property prop = getProperty(qname);    	
    	if(prop != null) {
    		prop.setValue(value);
    		return;
    	}
    	prop = new Property(qname, value);
    	if(prop != null) {
    		properties.add(prop);
    	}
    }
    public void addProperty(String localname, Object value)  {
    	if(localname.contains(getQName().getNamespace())) addProperty(new QName(localname), value);
    	else addProperty(new QName(getQName().getNamespace(), localname), value);
    }
    public Property getProperty(String qname) {
    	try {
    		QName q = QName.createQualifiedName(qname);
    		return getProperty(q);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    public Property getProperty(QName qname) {
    	if(qname != null && qname.getNamespace() != null && qname.getLocalName() != null) {
    		for(Property prop : this.getProperties()) {
    			if(prop.getQName() != null && prop.getQName().getNamespace() != null && prop.getQName().getLocalName() != null &&
    					prop.getQName().getNamespace().equals(qname.getNamespace()) && prop.getQName().getLocalName().equals(qname.getLocalName())) 
    				return prop;
    		}
    	}
    	return null;
    }
    public Object getPropertyValue(QName qname) {
    	Property prop = getProperty(qname.getNamespace(), qname.getLocalName());
    	if(prop != null && prop.getValue() != null) return prop.getValue();
    	return null;
    }
    public String getPropertyValueString(QName qname) {
    	Property prop = getProperty(qname.getNamespace(), qname.getLocalName());
    	if(prop != null && prop.getValue() != null) return prop.toString();
    	return null;
    }
    public boolean getPropertyValueBoolean(QName qname) {
		Property property =  getProperty(qname);
		if(property != null) {
			if(property.getValue() instanceof String) 
				return Boolean.valueOf((String)property.getValue());
			return (Boolean)property.getValue();
		}
		return false;
	}
    public Integer getPropertyValueInteger(QName qname) {
		Property property =  getProperty(qname);
		if(property != null) {
			if(property.getValue() instanceof String) 
				return Integer.valueOf((String)property.getValue());
			return (Integer)property.getValue();
		}
		return 0;
	}
    public Property getProperty(String namespace, String localname) {
    	return getProperty(new QName(namespace, localname));
    }
    public List<Property> getProperties(QName qname) {
    	List<Property> props = new ArrayList<Property>();
    	if(qname != null && qname.getNamespace() != null && qname.getLocalName() != null) {
    		for(Property prop : this.getProperties()) {
    			if(prop.getQName().getNamespace().equals(qname.getNamespace()) && prop.getQName().getLocalName().equals(qname.getLocalName())) 
    				props.add(prop);
    		}
    	}
    	return props;
    }
    public boolean hasProperty(String qname) {
    	try {
    		QName q = QName.createQualifiedName(qname);
    		return hasProperty(q);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    public boolean hasProperty(QName qname) {
    	for(Property prop : this.getProperties()) {
    		if(prop.getQName().equals(qname) && prop.getValue() != null)
    			return true;
    	}
    	return false;
    }
    public void removeProperty(QName qname) {
    	List<Property> props = new ArrayList<Property>();
    	for(Property prop : this.getProperties()) {
    		if(prop.getQName().equals(qname)) {
    			props.add(prop);
    		}
    	}
    	for(Property prop : props) this.getProperties().remove(prop);
    }
    public void setAssociations(List<Association> associations) {
    	for(Association association : associations) {
    		if(association.getSource().equals(this.getId()))
    			sourceAssociations.add(association);
    		else
    			targetAssociations.add(association);
    	}
    }
    public List<Association> getAssociations() {
    	List<Association> assocs = new ArrayList<Association>();
    	assocs.addAll(sourceAssociations);
    	assocs.addAll(targetAssociations);
    	return assocs;
    }
    public List<Association> getAssociations(QName qname) {
    	List<Association> assocs = new ArrayList<Association>();
    	for(Association a : sourceAssociations) {
    		if(a.getQName().equals(qname)) assocs.add(a);
    	}
    	for(Association a : targetAssociations) {
    		if(a.getQName().equals(qname)) assocs.add(a);
    	}
    	return assocs;
    }
    public List<Association> getSourceAssociations() {
    	return sourceAssociations;
    }
    public List<QName> getSourceAssociationQNames() {
    	List<QName> names = new ArrayList<QName>();
    	for(Association assoc : sourceAssociations) {
    		if(!names.contains(assoc.getQName())) names.add(assoc.getQName());
    	}
    	return names;
    }
    public Association getSourceAssociation(QName... qname) {
    	for(Association a : sourceAssociations) {
    		for(int i=0; i < qname.length; i++) {
    			if(a.getQName().equals(qname[i])) return a;
    		} 
    	}
    	return null;
    }
    public boolean containsSourceAssociation(QName qname) {
    	for(Association a : sourceAssociations) {
    		if(a.getQName().equals(qname)) return true;
    	}
    	return false;
    }
    public List<Association> getSourceAssociations(QName... qname) {
    	List<Association> assocs = new ArrayList<Association>();
    	for(Association a : sourceAssociations) {
    		for(int i=0; i < qname.length; i++) {
    			if(a.getQName().toString().equals(qname[i].toString())) 
    				assocs.add(a);
    		}    		
    	}
    	return assocs;
    }
    public List<Association> clearSourceAssociations(QName qname) {
    	return sourceAssociations;
    }
    public void setSourceAssociations(List<Association> sourceAssociations) {
        this.sourceAssociations = sourceAssociations;
    }
    
    public List<Association> getTargetAssociations() {
    	return targetAssociations;
    }
    public List<QName> getTargetAssociationQNames() {
    	List<QName> names = new ArrayList<QName>();
    	for(Association assoc : targetAssociations) {
    		if(!names.contains(assoc.getQName())) names.add(assoc.getQName());
    	}
    	return names;
    }
    public Association getTargetAssociation(QName... qname) {
    	for(Association a : targetAssociations) {
    		for(int i=0; i < qname.length; i++) {
    			if(a.getQName().equals(qname[i])) return a;
    		}  
    	}
    	return null;
    }
    public boolean containsTargetAssociation(QName qname) {
    	for(Association a : targetAssociations) {
    		if(a.getQName().equals(qname)) return true;
    	}
    	return false;
    }
    public List<Association> getTargetAssociations(QName... qname) {
    	List<Association> assocs = new ArrayList<Association>();
    	for(Association a : targetAssociations) {
    		for(int i=0; i < qname.length; i++) {
    			if(a.getQName().equals(qname[i])) assocs.add(a);
    		}    		
    	}
    	return assocs;
    }
    public void setTargetAssociations(List<Association> targetAssociations) {
        this.targetAssociations = targetAssociations;
    }
   
    public Association getParent() {
    	if(this.parentQName != null) {
    		return getTargetAssociation(this.parentQName);
    	} else if(targetAssociations.size() > 0) return targetAssociations.get(0);
        return null;
    }
    public List<Association> getChildren() {
    	if(this.childQNames.size() > 0) {
    		return getSourceAssociations((QName[])this.childQNames.toArray());
    	} else return getSourceAssociations();
    }    
    public boolean hasAssociation(QName qname) {
    	for(Association a : targetAssociations) {
    		if(a.getQName().toString().equals(qname.toString()))
    			return true;
    	}
		return false;
	}
	
	public QName getParentQName() {
		return parentQName;
	}
	public void setParentQName(QName parentQName) {
		this.parentQName = parentQName;
	}
	public List<QName> getChildQNames() {
		return childQNames;
	}
	public void setChildQNames(List<QName> childQNames) {
		this.childQNames = childQNames;
	}	
	public Map<String,Object> getPropertyMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		for(Property prop : properties) {
			map.put(prop.getQName().getLocalName(), prop.toString());
		}
		return map;
	}
	@Override
	public String toString() {
		return "[id=" + id + ", qname=" + qname + "]";
	}
	
}