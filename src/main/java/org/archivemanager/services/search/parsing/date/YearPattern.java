package org.archivemanager.services.search.parsing.date;
import org.codehaus.jparsec.pattern.Pattern;

/**
* Created by babar on 8/24/14.
*/
public class YearPattern extends Pattern {
    @Override
    public int match(CharSequence src, int begin, int end) {
        int length = end - begin;

        if(length != 4 || !isInt(src.subSequence(begin, end).toString()))
            return MISMATCH;

        return 4;
    }

    private boolean isInt(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isYear(String string) {
        return match(string, 0, string.length()) != MISMATCH;
    }
}