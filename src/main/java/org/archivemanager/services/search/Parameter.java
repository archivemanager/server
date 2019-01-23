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
package org.archivemanager.services.search;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Parameter {
	public String name;
	public Object value;
	public String type = TYPE_EQUALS;
	
	public static final String TYPE_EQUALS = "EQUALS";
	public static final String TYPE_GREATER = "GREATER";
	public static final String TYPE_LESS = "LESS";
	
	
	public Parameter() {}
	public Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	public Parameter(String type, String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void fromJsonObject(JsonObject object) {
		if(object.containsKey("name")) setName(object.getString("name"));
		if(object.containsKey("value")) setValue(object.getString("value"));
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getName() != null) builder.add("name", getName());
		if(getValue() != null) builder.add("value", String.valueOf(getValue()));
		return builder.build();
	}
}
