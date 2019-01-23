package org.archivemanager.services.security;

import org.archivemanager.data.io.Base64;
import org.archivemanager.data.io.CharArrayBuffer;

public class BasicAuthenticationClient {
	public static final String PROXY_AUTH_RESP = "Proxy-Authorization";
	public static final String WWW_AUTH_RESP = "Authorization";
	
	
	public static String generateHeader(String username, String password, boolean proxy) {
		StringBuilder tmp = new StringBuilder();
        tmp.append(username);
        tmp.append(":");
        tmp.append((password == null) ? "null" : password);

        byte[] base64password = Base64.encode(tmp.toString().getBytes());

        CharArrayBuffer buffer = new CharArrayBuffer(32);
        /*
        if (proxy) {
            buffer.append(PROXY_AUTH_RESP);
        } else {
            buffer.append(WWW_AUTH_RESP);
        }
        */
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return buffer.toString();
	}
}
