package org.archivemanager.services.search.parsing.date;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.error.ParserException;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.pattern.CharPredicates;
import org.codehaus.jparsec.pattern.Patterns;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by babar on 8/20/14.
 */
public class DateParser {
	private static final Set<String> ops = new HashSet<>(Arrays.asList("[", "]", ".", ":", "/", "\\", ",", "-"));

    private static final Terminals operatorsAndKeywords = Terminals.caseInsensitive(Parsers.or(
                    DateParserFactory.yearParser(),
                    DateParserFactory.numericMonthDayParser(),
                    Scanners.pattern(Patterns.isChar(CharPredicates.IS_ALPHA_NUMERIC), "alpha_numeric").many().source(),
                    Scanners.IDENTIFIER), ops.toArray(DateParserFactory.emptyStringArray),
                    DateParserFactory.addArrays(DateParserFactory.dateQueryKeywords,
                    DateParserFactory.months, DateParserFactory.weekdays, DateParserFactory.monthDaySuffixArray), new
                    Map<String, Object>() {
                        @Override
                        public Object map(String s) {

                            if(DateParserFactory.yearPattern.isYear(s))
                                return Tokens.fragment(s, DateParserFactory.YEAR);

                            if(DateParserFactory.monthDayPattern.isMonthDay(s))
                                return Tokens.fragment(s, DateParserFactory.MONTH_DAY);

                            return Tokens.fragment(s, Tokens.Tag.IDENTIFIER);
                        }
                    });

    private static final Terminals numbers = Terminals.caseInsensitive(
            Parsers.sequence(Scanners.isChar('0').many(), Scanners.DEC_INTEGER),
            ops.toArray(DateParserFactory.emptyStringArray), DateParserFactory.emptyStringArray, new Map<String, Object>() {
                @Override
                public Object map(String s) {
                    return Tokens.fragment(s, Tokens.Tag.INTEGER);
                }
            });

    private static Parser<Iterable<Date>> numericDateParser = createNumericDateParser();
    private static Parser<Date> dateParser = createDateParser();

    private static Parser<Date> numericYearMonthDateParser = createNumericYearMonthDateParser();

    private static Parser<Date> createDateParser(){
        Parser<?> tokenizer = operatorsAndKeywords.tokenizer();

        return DateParserFactory.dateParser(operatorsAndKeywords).from(
                tokenizer, Scanners.isChar(' ').optional());
    }

    private static Parser<Iterable<Date>> createNumericDateParser(){
        Parser<?> tokenizer = numbers.tokenizer();

        return DateParserFactory.numericDateParser(numbers).from(
                tokenizer, Scanners.isChar(' ').optional());
    }

    private static Parser<Date> createNumericYearMonthDateParser(){
        Parser<?> tokenizer = numbers.tokenizer();

        return DateParserFactory.numericMonthYearDateParser(numbers).from(
                tokenizer, Scanners.isChar(' ').optional());
    }

    private static Date parseNumericDate(String date) {
        try {
            Iterable<Date> dateIterable = numericDateParser.parse(date);
            if(dateIterable.iterator().hasNext())
            	return dateIterable.iterator().next();

        }catch (ParserException e) {
            return null;
        }
        return null;
    }

    private static Date parseNumericYearMonthDate(String date) {
        try {
            return numericYearMonthDateParser.parse(date);
        }catch (ParserException e) {
            return null;
        }
    }

    private static Date parseDate(String date) {
        try {
            return dateParser.parse(date);
        }catch (ParserException e) {
            return null;
        }
    }

    public Date parse(String date) {
    	
        Date result = parseNumericDate(date);

        if(result == null)
            result = parseNumericYearMonthDate(date);

        if(result == null)
            result = parseDate(date);

        return result;
    }
}
