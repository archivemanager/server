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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.archivemanager.models.system.QName;


public class Property implements java.io.Serializable {
	private static final long serialVersionUID = 2824085052507363147L;
	private QName qname;
    private Object value = "";
    
    public Property() {}
    public Property(QName qname) {
    	this.qname = qname;
	}
    public Property(QName qname, Object value) {
    	this.qname = qname;
		setValue(value);
	}
    
    public QName getQName() {
        return qname;
    }
    
    public void setQname(QName qname) {
    	this.qname = qname;
    }
        
    public String toString() {
    	return String.valueOf(value);
    }	
	public Object getValue() {
    	return value;
    }
    public void setValue(Object value) {
    	if(value != null) this.value = value;
    }
		
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getQName() != null) builder.add("qname", getQName().toString());
		if(getValue() != null) builder.add("value", String.valueOf(getValue()));
		return builder.build();
	}
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if (obj instanceof Property){
            Property that = (Property) obj;
            if(this.value.equals(that.getValue())) return true;
        }
        return false;
    }
}
