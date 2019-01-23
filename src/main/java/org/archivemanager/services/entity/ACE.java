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


public class ACE {
	private Long id;
	private Entity node;
	private int permission;
	private String authority;
	
	@SuppressWarnings("unused")
	public enum ACEType {
		NO_ACCESS {
			public int getId() {return 0;}
		},
		READ {
			public int getId() {return 1;}
		},
		WRITE {
			public int getId() {return 2;}
		},
		CREATE {
			public int getId() {return 3;}
		},
		DELETE {
			public int getId() {return 4;}
		},
		OWNER {
			public int getId() {return 5;}
		}
	}
	
	public ACE() {}
	public ACE(Entity node, int permission, String authority) {
		this.node = node;
		this.permission = permission;
		this.authority = authority;
	}
	
	public ACEType getACEType() {
		if(permission == 0) return ACEType.NO_ACCESS;
		if(permission == 1) return ACEType.READ;
		if(permission == 2) return ACEType.WRITE;
		if(permission == 3) return ACEType.CREATE;
		if(permission == 4) return ACEType.DELETE;
		if(permission == 5) return ACEType.OWNER;
		throw new IllegalArgumentException("Unknown ace type "+permission);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Entity getNode() {
		return node;
	}
	public void setNode(Entity node) {
		this.node = node;
	}
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
		
}
