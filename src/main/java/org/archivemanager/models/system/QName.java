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
package org.archivemanager.models.system;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.archivemanager.exception.InvalidQualifiedNameException;


public class QName  implements java.io.Serializable {
	private static final long serialVersionUID = -3703913360078545820L;
	private final static Logger log = Logger.getLogger(QName.class.getName());
	private Long id;
    private String namespace;
    private String localName;
    private String prefix;

    public static final char NAMESPACE_PREFIX = ':';
    public static final char NAMESPACE_BEGIN = '{';
    public static final char NAMESPACE_END = '}';
    
    public QName() {}
    public QName(String value) {
    	try {
    		QName q = createQualifiedName(value);
    		this.namespace = q.namespace;
    		this.localName = q.localName;
    	} catch(InvalidQualifiedNameException e) {
    		log.log(Level.SEVERE, "error setting QName value "+value, e);
    	}
    }
    public QName(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
        if(localName.contains(namespace))
    		log.log(Level.SEVERE, "localName contains namespace");
    }
    public QName(String prefix, String namespace, String localName) {
        this.prefix = prefix;
    	this.namespace = namespace;
        this.localName = localName;
        if(localName.contains(namespace))
    		log.log(Level.SEVERE, "localName contains namespace");
    }
    
    public String getPrefix() {
    	return prefix;
    }
    public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
    
	public static QName createQualifiedName(String namespace, String localName) throws InvalidQualifiedNameException {
    	if(namespace == null || localName == null)
    		throw new InvalidQualifiedNameException("Argument qname is mandatory");
    	if(localName.contains(namespace))
    		throw new InvalidQualifiedNameException("localName contains namespace");
    	return new QName(namespace, localName);
    }
    public static QName createQualifiedName(String qname) throws InvalidQualifiedNameException {
    	if (qname == null || qname.length() == 0) {
            throw new InvalidQualifiedNameException("Argument qname is mandatory qname:" + qname);
        }
        String namespaceURI = null;
        String localName = null;
        
        /**parse the old format if it starts properly, otherwise new format **/
        if(qname.charAt(0) == NAMESPACE_BEGIN) {
        	int namespaceBegin = qname.indexOf(NAMESPACE_BEGIN);
            int namespaceEnd = -1;            
            if(namespaceBegin != -1) {
                if(namespaceBegin != 0) {
                    throw new InvalidQualifiedNameException("QName '" + qname + "' must start with a namespaceURI");
                }
                namespaceEnd = qname.indexOf(NAMESPACE_END, namespaceBegin + 1);
                if(namespaceEnd == -1) {
                    throw new InvalidQualifiedNameException("QName '" + qname + "' is missing the closing namespace "+NAMESPACE_END+" token");
                }
                namespaceURI = qname.substring(namespaceBegin + 1, namespaceEnd);
            } else {
            	namespaceBegin = 0;
            	namespaceEnd = qname.lastIndexOf("_");
            	namespaceURI = qname.substring(namespaceBegin, namespaceEnd);
            }            
            // Parse name
            if(namespaceEnd > -1) {
    	        localName = qname.substring(namespaceEnd + 1);
    	        if(localName == null || localName.length() == 0) {
    	            throw new InvalidQualifiedNameException("QName '" + qname + "' must consist of a local name");
    	        }
            }
        } else {        
	        // Parse namespace
	        int namespaceEnd = qname.lastIndexOf("_1_0_");
	        if(namespaceEnd == -1) namespaceEnd = qname.lastIndexOf("_2_0_");
	        namespaceURI = qname.substring(0, namespaceEnd + 4);
	                
	        // Parse name
	        if(namespaceEnd > -1) {
		        localName = qname.substring(namespaceEnd + 5);
		        if(localName == null || localName.length() == 0) {
		            throw new InvalidQualifiedNameException("QName '" + qname + "' must consist of a local name");
		        }
	        } 
        }
        if(localName == null) 
        	throw new InvalidQualifiedNameException("invalid QName : localName null for "+qname);
        return new QName(namespaceURI, localName);
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLocalName() {
        if(this.localName != null) return this.localName;
        else return "";
    }    
    public void setLocalName(String localName) {
        this.localName = localName;
    }
    
    public void fromJsonObject(JsonObject object) {
		if(object.containsKey("namespace")) setNamespace(object.getString("namespace"));
		if(object.containsKey("localName")) setLocalName(object.getString("localName"));
		if(object.containsKey("prefix")) setPrefix(object.getString("prefix"));
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();	
		builder.add("namespace", namespace);
		builder.add("localName", localName);
		if(prefix != null) builder.add("prefix", prefix);
		return builder.build();
	}    
    
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localName == null) ? 0 : localName.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QName other = (QName) obj;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
	public String toString() {
    	return new StringBuilder(80).append(namespace).append("_").append(localName).toString();
    }
    public String toQualifiedString() {
    	return new StringBuilder(80).append(NAMESPACE_BEGIN).append(namespace).append(NAMESPACE_END).append(localName).toString();
    }
}


