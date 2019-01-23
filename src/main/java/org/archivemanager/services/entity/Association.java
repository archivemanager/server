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
import java.util.List;

import org.archivemanager.models.system.QName;


public class Association implements Serializable {
	private static final long serialVersionUID = -8451920306136345723L;
	private Long id;
	private String uid;
    private QName qname;
    private Long source;
    private QName sourceName;
    private String sourceUid;
    private Long target;
    private QName targetName;
    private String targetUid;
    private boolean cascades = false;
    private long created;
    private long modified;
    private long creator;
    private long modifier;
    private long accessed;
	private long user;
    private List<Property> properties = new ArrayList<Property>(0);
    
    
    public Association() {}
    public Association(QName qname) {
    	this.qname = qname;
    }
    public Association(long id, QName qname) { 
    	this.id = id;
    	this.qname = qname;
    }    
    public Association(QName qname, long source, long target) {    	
    	this.qname = qname;
    	this.source = source;
    	this.target = target;
    }
    public Association(long id, QName qname, long source, long target) {
    	this.id = id;
    	this.qname = qname;
    	this.source = source;
    	this.target = target;
    }
    public Association(QName qname, String sourceUid, String targetUid) {
    	this.qname = qname;
    	this.sourceUid = sourceUid;
    	this.targetUid = targetUid;
    }
    public Association(QName qname, QName sourceName, QName targetName) {
    	this.qname = qname;
    	this.sourceName = sourceName;
    	this.targetName = targetName;
    }
    public Association(QName qname, Entity sourceEntity, Entity targetEntity) {
    	this.qname = qname;
    	if(sourceEntity.getId() != null) this.source = sourceEntity.getId();
    	if(targetEntity.getId() != null) this.target = targetEntity.getId();
    	if(sourceEntity.getQName() != null) this.sourceName = sourceEntity.getQName();
    	if(targetEntity.getQName() != null) this.targetName = targetEntity.getQName();
    	if(sourceEntity.getUid() != null) this.sourceUid = sourceEntity.getUid();
    	if(targetEntity.getUid() != null) this.targetUid = targetEntity.getUid();
    }
    public Association(QName qname, QName sourceName, long source, QName targetName, long target) {
    	this.qname = qname;
    	this.sourceName = sourceName;
    	this.targetName = targetName;
    	this.source = source;
    	this.target = target;
    }
    public Association(Long id, QName qname, QName sourceName, long source, QName targetName, long target) {
    	this.id = id;
    	this.qname = qname;
    	this.sourceName = sourceName;
    	this.targetName = targetName;
    	this.source = source;
    	this.target = target;
    }
    
    public Long getId() {
        return this.id;
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
	public QName getSourceName() {
		return sourceName;
	}
	public void setSourceName(QName sourceName) {
		this.sourceName = sourceName;
	}
	public QName getTargetName() {
		return targetName;
	}
	public void setTargetName(QName targetName) {
		this.targetName = targetName;
	}
	public QName getQName() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public Long getSource() {
		return source;
	}
	public void setSource(Long source) {
		this.source = source;
	}
	/*
	public Entity getSourceEntity() {
		return sourceEntity;
	}
	public void setSourceEntity(Entity sourceEntity) {
		this.sourceEntity = sourceEntity;
		if(sourceEntity.getId() != null) this.source = sourceEntity.getId();
		if(sourceEntity.getQName() != null) this.sourceName = sourceEntity.getQName();
	}
	*/
	public Long getTarget() {
		return target;
	}
	public void setTarget(Long target) {
		this.target = target;
	}
	/*
	public Entity getTargetEntity() {
		return targetEntity;
	}
	public void setTargetEntity(Entity targetEntity) {
		this.targetEntity = targetEntity;
		if(targetEntity.getId() != null) this.target = targetEntity.getId();
		if(targetEntity.getQName() != null) this.targetName = targetEntity.getQName();
	}
	*/
	public List<Property> getProperties() {
        return this.properties;
    }
    public String getSourceUid() {
		return sourceUid;
	}
	public void setSourceUid(String sourceUid) {
		this.sourceUid = sourceUid;
	}
	public String getTargetUid() {
		return targetUid;
	}
	public void setTargetUid(String targetUid) {
		this.targetUid = targetUid;
	}
	public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    public boolean cascades() {
		return cascades;
	}
	public void setCascades(boolean cascades) {
		this.cascades = cascades;
	}	
	public void addProperty(QName qname, Object value) {
    	for(Property prop : properties) {
    		if(prop.getQName().equals(qname)) {
    			prop.setValue(value);
    			return;
    		}
    	}
    	Property prop = new Property(qname, value);
    	//prop.setNodeAssoc(this);
    	properties.add(prop);
    }
    public Property getProperty(QName qname) {
    	if(qname != null && qname.getNamespace() != null && qname.getLocalName() != null) {
    		for(Property prop : this.getProperties()) {
    			if(prop.getQName().getNamespace().equals(qname.getNamespace()) && prop.getQName().getLocalName().equals(qname.getLocalName())) 
    				return prop;
    		}
    	}
    	return null;
    }
    public String getPropertyValue(QName qname) {
    	Property prop = getProperty(qname);
    	if(prop != null) return prop.toString();
    	return "";
    }
    public boolean hasProperty(QName qname) {
    	for(Property prop : this.getProperties()) {
    		if(prop.getQName().equals(qname))
    			return true;
    	}
    	return false;
    }
    public void removeProperty(QName qname) {
    	for(Property prop : this.getProperties()) {
    		if(prop.getQName().equals(qname)) {
    			this.getProperties().remove(prop);
    		}
    	}
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
	
	@Override
	public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Association)) {
            return false;
        }
        Association that = (Association) obj;
        if(this.getId() != null && that.getId() != null && this.getId().equals(that.getId()))
            return true;
        
        return false;
    }
	@Override
	public String toString() {
		return "[id=" + id + ", qname=" + qname + ", source=" + source + ", sourceName=" + sourceName
				+ ", target=" + target + ", targetName=" + targetName + "]";
	}
	
}


