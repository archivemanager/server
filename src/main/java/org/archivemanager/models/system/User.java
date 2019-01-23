package org.archivemanager.models.system;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;


public class User {
	private Long id;
	private Long xid;
	private String name;	
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String province;
	private String country;
	private String postalCode;
	private String phoneNumber;
	private String email;
	private String website;
	private String avatar;
	private String password;
	private boolean active;
	private List<Role> roles = new ArrayList<Role>();
	private List<Group> groups = new ArrayList<Group>();
	private List<Permission> permissions = new ArrayList<Permission>();
	
	
	public boolean hasRole(String name) {
		for(Role r : roles) {
			if(r.getName().equals(name))
				return true;
		}
		return false;
	}
	public boolean isAdministrator() {
		for(Role r : getRoles()) {
			if(r.getName().equals("Administrator"))
				return true;
		}
		return false;
	}
	public boolean isGuest() {
		return false;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getXid() {
		return xid;
	}
	public void setXid(Long xid) {
		this.xid = xid;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		result = prime * result + ((province == null) ? 0 : province.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		result = prime * result + ((xid == null) ? 0 : xid.hashCode());
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
		User other = (User) obj;
		if (active != other.active)
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (permissions == null) {
			if (other.permissions != null)
				return false;
		} else if (!permissions.equals(other.permissions))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (postalCode == null) {
			if (other.postalCode != null)
				return false;
		} else if (!postalCode.equals(other.postalCode))
			return false;
		if (province == null) {
			if (other.province != null)
				return false;
		} else if (!province.equals(other.province))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		if (xid == null) {
			if (other.xid != null)
				return false;
		} else if (!xid.equals(other.xid))
			return false;
		return true;
	}
	public String toJson() {
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(writer);				
		JsonObject obj = toJsonObject();		
		jsonWriter.write(obj);
		return writer.toString();
	}
    public JsonObject toSimpleJsonObject() {
    	JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getId() != null) builder.add("id", getId());
		if(getXid() != null) builder.add("xid", getXid());
		if(getName() != null) builder.add("name", getName());
		if(getEmail() != null) builder.add("email", getEmail());
		return builder.build();
    }
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getId() != null) builder.add("id", getId());
		if(getXid() != null) builder.add("xid", getXid());
		if(getName() != null) builder.add("name", getName());
		if(getEmail() != null) builder.add("email", getEmail());
		JsonArrayBuilder roleBuilder = Json.createArrayBuilder();
		for(Role role : roles) {
			roleBuilder.add(role.toSimpleJsonObject());
		}
		builder.add("roles", roleBuilder);
		JsonArrayBuilder groupBuilder = Json.createArrayBuilder();
		for(Group group : groups) {
			groupBuilder.add(group.toJsonObject());
		}
		builder.add("groups", groupBuilder);
		JsonArrayBuilder permissionBuilder = Json.createArrayBuilder();
		for(Permission permission : permissions) {
			permissionBuilder.add(permission.toJsonObject());
		}
		builder.add("groups", groupBuilder);
		return builder.build();
	}
	public void fromJson(JsonObject object) {
		if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
		if(object.containsKey("xid")) setXid(object.getJsonNumber("xid").longValue());
		if(object.containsKey("name")) setName(object.getString("name"));
		if(object.containsKey("roles")) {
			JsonArray roles = object.getJsonArray("roles");
			for(JsonValue seedValue : roles) {
				Role role = new Role();
				role.fromJson((JsonObject)seedValue);
				getRoles().add(role);
			}
		}
		if(object.containsKey("groups")) {
			JsonArray groups = object.getJsonArray("groups");
			for(JsonValue seedValue : groups) {
				Group group = new Group();
				group.fromJson((JsonObject)seedValue);
				getGroups().add(group);
			}
		}
	}
}
