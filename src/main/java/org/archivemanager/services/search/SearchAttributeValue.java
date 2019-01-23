/*
 * Copyright (C) 2010 Heed Technology Inc.
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


public class SearchAttributeValue {
	//private SearchAttribute attribute;
	private String name;
	private String query;
	private int count;
	private String value;
	
	
	public SearchAttributeValue(JsonObject obj) {
		fromJsonObject(obj);
	}
	public SearchAttributeValue(String name) {
		this.name = name;
	}
	public SearchAttributeValue(String name, String query) {
		this.name = name;
		this.query = query;
	}
	public SearchAttributeValue(String name,String query, int count) {
		this.name = name;
		this.query = query;
		this.count = count;
	}
	public SearchAttributeValue(String name,String query, String value) {
		this.name = name;
		this.query = query;
		this.value = value;
	}
	/*
	public SearchAttribute getAttribute() {
		return attribute;
	}
	public void setAttribute(SearchAttribute attribute) {
		this.attribute = attribute;
	}
	*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getCount() {
		return count;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public void fromJsonObject(JsonObject object) {
		if(object.containsKey("name")) setName(object.getString("name"));
		if(object.containsKey("query")) setQuery(object.getString("query"));
		if(object.containsKey("count")) setCount(object.getJsonNumber("count").intValue());
		if(object.containsKey("value")) setValue(object.getString("value"));
		//if(object.containsKey("attribute")) setAttribute(new SearchAttribute(object.getJsonObject("attribute")));
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();	
		builder.add("name", getName());
		builder.add("query", getQuery());
		builder.add("count", getCount());
		builder.add("value", getValue());
		//builder.add("attribute", getAttribute().toJsonObject());
		return builder.build();
	}
}
