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
package org.archivemanager.data;

import java.io.Serializable;


public class IDTypeName implements Serializable, Comparable<IDTypeName> {
	private static final long serialVersionUID = 5709917316183592411L;
	private String id;
	private String type;
	private String Name;
	
	public IDTypeName() {}
	public IDTypeName(String id, String type, String name) {
		this.id = id;
		this.type = type;
		this.Name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public boolean equals(Object obj) {
		if(obj == null) {
            return false;
        } else if(obj == this) {
            return true;
        } else if(!(obj instanceof IDTypeName)) {
            return false;
        }
		IDTypeName that = (IDTypeName) obj;
        return this.getName().equals(that.getName());
	}
	@Override
	public int compareTo(IDTypeName o) {
		return this.getName().compareTo(o.getName());
	}
}
