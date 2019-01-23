package org.archivemanager.services.search.parsing;

/**
 * Created by babar on 3/1/15.
 */
public class ParseException extends Exception {
    ParseException() {}

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
