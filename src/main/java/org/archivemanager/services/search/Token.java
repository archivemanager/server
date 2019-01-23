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

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.search.Definition;



public class Token implements TokenTypes {
	protected String value;
	protected int type = NULL;
	protected List<Integer> subTypes = new ArrayList<Integer>();
	protected List<String> subValues = new ArrayList<String>();
	protected List<Token> subTokens = new ArrayList<Token>();
	protected int occurance = MUST;
	protected String name = "";
	protected boolean inParens = false;
	protected List<Definition> definitions = new ArrayList<Definition>();
	
	public static final int MUST_NOT = 0;
	public static final int MUST = 1;

	public Token() {}
	
	public Token(Definition definition) {
		this(definition.getValue());
		this.name = definition.getName();
		this.type = definition.getType();
		this.definitions.add(definition);
	}
	public Token(Definition definition, int type) {
		this(definition.getValue());
		this.name = definition.getName();
		this.type = definition.getType();
		this.definitions.add(definition);
		this.type = type;
	}
	public Token(List<Definition> definitions, int type) {
		this.definitions = definitions;
		this.type = type;
	}
	public Token(String value) {
		this.value = value;
	}
	public Token(String name, String value) {
		this(value);
		this.name = name;
	}
	public Token(String name, String value, int type) {
		this(value);
		this.name = name;
		this.type = type;
	}
	
	
	public boolean isType(int type) {
		if(subTypes.size() > 1) {
			for(Integer t : subTypes)
				if(t == type)
					return true;
		} else
			return this.type == type;
		return false;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOccurance() {
		return occurance;
	}
	public void setOccurance(int occurance) {
		this.occurance = occurance;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Integer> getSubTypes() {
		return subTypes;
	}
	public void setSubTypes(List<Integer> subTypes) {
		this.subTypes = subTypes;
	}
	public List<String> getSubValues() {
		return subValues;
	}
	public void setSubValues(List<String> subValues) {
		this.subValues = subValues;
	}
	public boolean inParens() {
		return inParens;
	}
	public void setInParens(boolean inParens) {
		this.inParens = inParens;
	}
	public List<Token> getSubTokens() {
		return subTokens;
	}
	public void setSubTokens(List<Token> subTokens) {
		this.subTokens = subTokens;
	}
	public List<Definition> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}
	
}