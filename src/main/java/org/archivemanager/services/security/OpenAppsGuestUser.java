package org.archivemanager.services.security;

import org.archivemanager.models.system.User;

public class OpenAppsGuestUser extends User {

	
	public OpenAppsGuestUser() {
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
