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
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.archivemanager.services.search.SearchAttributeValue;

public class SearchAttribute {
	private String name;
	private String sort;
	private int size;
	private long minDocCount = 1;
	private List<SearchAttributeValue> values = new ArrayList<SearchAttributeValue>();
	
	public static final String NAMES_TYPE = "names";
	public static final String SUBJECTS_TYPE = "subjects";
	public static final String DEFINED_TYPE = "defined";
	public static final String COLLECTION_TYPE = "collections";
	
	public static final String ALPHA_SORT = "alpha";
	public static final String COUNT_SORT = "count";
	
	public SearchAttribute() {}
	public SearchAttribute(String name) {
		this.name = name;
	}
	public SearchAttribute(JsonObject obj) {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
		
	public void addValue(String value) {
		SearchAttributeValue val = findSearchAttributeValue(value);
		val.setCount(val.getCount()+1);
	}
	public List<SearchAttributeValue> getValues() {
		return values;
	}
	public void setValues(List<SearchAttributeValue> values) {
		this.values = values;
	}
	public void addSearchAttributeValue(String name, String query) {
		SearchAttributeValue value = findSearchAttributeValue(name);
		value.setQuery(query);
	}
	protected SearchAttributeValue findSearchAttributeValue(String name) {
		for(int i=0; i < values.size(); i++) {
			if(values.get(i).getName().equals(name)) return values.get(i);
		}
		SearchAttributeValue att = new SearchAttributeValue(name);
		values.add(att);
		//att.setAttribute(this);
		return att;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public long getMinDocCount() {
		return minDocCount;
	}
	public void setMinDocCount(long minDocCount) {
		this.minDocCount = minDocCount;
	}
	
	public void fromJsonObject(JsonObject object) {
		if(object.containsKey("name")) setName(object.getString("name"));
		if(object.containsKey("sort")) setSort(object.getString("sort"));
		if(object.containsKey("values")) {
			JsonArray valuesObjs = object.getJsonArray("values");
			for(JsonValue valueValue : valuesObjs) {
				SearchAttributeValue result = new SearchAttributeValue((JsonObject)valueValue);
				getValues().add(result);
			}
    	}
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();	
		builder.add("name", getName());
		builder.add("sort", getSort());
		JsonArrayBuilder valueBuilder = Json.createArrayBuilder();
		for(SearchAttributeValue value : values) {
			valueBuilder.add(value.toJsonObject());
		}
		builder.add("values", valueBuilder);
		return builder.build();
	}
}
