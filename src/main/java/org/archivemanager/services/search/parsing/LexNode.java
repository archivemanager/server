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
package org.archivemanager.services.search.parsing;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.archivemanager.services.search.Definition;


public class LexNode {
	private char value;
	private LexNode parent;
	private Vector<LexNode> children;
	private List<Definition> definitions = new ArrayList<Definition>();
	private boolean caseSensitive = false;
	
	public LexNode() {
		children = new Vector<LexNode>();
	}
	public LexNode(char value, LexNode parent, boolean caseSensitive) {
		this.value = value;
		this.parent = parent;
		this.caseSensitive = caseSensitive;
		parent.addChild(this);
		children = new Vector<LexNode>();
	}
	
	public char getValue() {
		return value;
	}
	public String getDefinedValue() {
		LexNode node = this;
		StringBuffer value = new StringBuffer();
		while(node.getValue() != '\u0000') {
			value.append(node.getValue());
			node = node.getParent();
		}
		return value.reverse().toString();
	}
	public boolean childExists(char value) {
		for(int i=0; i < children.size(); i++) {
			if(children.get(i).getValue() == value) return true;
		}
		return false;
	}
	public LexNode getChild(char value) {
		for(int i=0; i < children.size(); i++) {
			LexNode child = children.get(i);
			if(child.match(value)) 
				return child;
		}
		return null;
	}
	public boolean match(char value) {
		if(!isCaseSensitive()) {
			if(Character.toUpperCase(getValue()) == value || Character.toLowerCase(getValue()) == value) 
				return true;
		} else {
			if(getValue() == value) return true;
		}
		return false;
	}
	public void addChild(LexNode node) {
		children.add(node);
	}
	public Vector<LexNode> getChildren() {
		return children;
	}
	public LexNode getParent() {
		return parent;
	}
	public void setParent(LexNode parent) {
		this.parent = parent;
	}
	public boolean hasDefinition() {
		return definitions.size() > 0;
	}
	public void addDefinition(Definition def) {
		definitions.add(def);
	}
	public List<Definition> getDefinitions() {
		return definitions;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof LexNode && ((LexNode)obj).getValue() == value) return true;
		return false;
	}
}