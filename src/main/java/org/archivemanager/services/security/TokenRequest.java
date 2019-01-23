package org.archivemanager.services.security;

import java.io.Serializable;

public class  TokenRequest implements Serializable {
    private static final long serialVersionUID = -8445943548965154778L;
    private String email;
    private String password;

    
    public TokenRequest() {
        super();
    }
    public TokenRequest(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}