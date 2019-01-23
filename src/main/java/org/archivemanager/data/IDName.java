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


public class IDName implements Serializable, Comparable<IDName> {
	private static final long serialVersionUID = 5709917316183592411L;
	private String id;
	private String name;
	private String type;
	
	public IDName() {}
	public IDName(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public IDName(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean equals(Object obj) {
		if(obj == null) {
            return false;
        } else if(obj == this) {
            return true;
        } else if(!(obj instanceof IDName)) {
            return false;
        }
		IDName that = (IDName) obj;
        return this.getName().equals(that.getName());
	}
	@Override
	public int compareTo(IDName o) {
		return this.getName().compareTo(o.getName());
	}
	@Override
	public String toString() {
		return name;
	}
}
