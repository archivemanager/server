package org.archivemanager.services.security;

import org.archivemanager.models.system.User;


public class GuestUser extends User {

	
	public GuestUser() {
		setId(0L);
	}
	
	@Override
	public boolean hasRole(String role) {
		return false;
	}

	@Override
	public boolean isAdministrator() {
		return false;
	}

	@Override
	public boolean isGuest() {
		return true;
	}
	
}
