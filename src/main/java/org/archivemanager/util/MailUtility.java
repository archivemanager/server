package org.archivemanager.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.archivemanager.data.validation.EmailValidator;


public class MailUtility {

	
	public static void openDesktopMail(String subject, String body) {
		URI uriMailTo = null;
        try {
        	uriMailTo = new URI("mailto", "?subject="+subject.replace("&", "and")+"&body="+body.replace("&", "and"), null);
            Desktop.getDesktop().mail(uriMailTo);
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(URISyntaxException use) {
            use.printStackTrace();
        }
	}
	public static boolean isValid(String email) {
		return EmailValidator.getInstance().isValid(email);
	}
}
