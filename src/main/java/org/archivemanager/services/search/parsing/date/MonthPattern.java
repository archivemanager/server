package org.archivemanager.services.search.parsing.date;
import org.codehaus.jparsec.pattern.Pattern;

/**
* Created by babar on 8/24/14.
*/
public class MonthPattern extends Pattern {
    @Override
    public int match(CharSequence src, int begin, int end) {
        int length = end - begin;

        if(length <= 0 || length > 2)
            return MISMATCH;

        String string = src.subSequence(begin, begin +2).toString();

        switch (string){
            case "01" :case "02" :case "03" : case "04" :case "05" :case "06" :case "07" :case "08" :case "09" :
            case "10" :case "11" :case "12" :case "13" : case "14" :case "15" :case "16" :case "17" :case "18" :
            case "19" :case "20" :case "21" :case "22" :case "23" : case "24" :case "25" :case "26" :case "27" :
            case "28" :case "29" :case "30" :case "31" :
                return 2;
        }

        string = src.subSequence(begin, begin + 1).toString();

        switch (string) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                return 1;
        }

        return MISMATCH;
    }

    public boolean isMonthDay(String string) {
        return match(string, 0, string.length()) != MISMATCH;
    }
}