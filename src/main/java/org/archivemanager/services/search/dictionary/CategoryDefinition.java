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
package org.archivemanager.services.search.dictionary;
import java.util.BitSet;
import java.util.List;

import org.archivemanager.services.search.Token;


public class CategoryDefinition extends DefinitionSupport {
	private List<String> ids;
	private BitSet bits;
	
	
	public CategoryDefinition(String name, String value) {
		super(Token.CATG, name, value);
	}
	
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public BitSet getBits() {
		return bits;
	}
	public void setBits(BitSet bits) {
		this.bits = bits;
	}
	
	@Override
	public String toString() {
		return "Category";
	}	
}
