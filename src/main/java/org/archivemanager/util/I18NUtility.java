package org.archivemanager.util;

import java.util.Locale;
import java.util.StringTokenizer;

public class I18NUtility {
	/**
     * Thread-local containing the general Locale for the current thread
     */
    private static ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
	
    
	public static Locale parseLocale(String localeStr) {
        if(localeStr == null) {
            return null; 
        }
        Locale locale = Locale.getDefault();
        StringTokenizer t = new StringTokenizer(localeStr, "_");
        int tokens = t.countTokens();
        if (tokens == 1) {
           locale = new Locale(t.nextToken());
        } else if (tokens == 2) {
           locale = new Locale(t.nextToken(), t.nextToken());
        } else if (tokens == 3) {
           locale = new Locale(t.nextToken(), t.nextToken(), t.nextToken());
        }        
        return locale;
    }
	public static String parseLocale(Locale locale) {
		String localeStr = locale.toString();
        if (localeStr.length() < 6) {
            localeStr += "_";
        }
        return localeStr;
	}
	/**
     * Get the general local for the current thread, will revert to the default locale if none 
     * specified for this thread.
     * 
     * @return  the general locale
     */
    public static Locale getLocale() {
        Locale locale = threadLocale.get(); 
        if (locale == null)
        {
            // Get the default locale
            locale = Locale.getDefault();
        }
        return locale;
    }
}
