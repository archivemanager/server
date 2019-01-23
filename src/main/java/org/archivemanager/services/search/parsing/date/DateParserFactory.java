package org.archivemanager.services.search.parsing.date;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.functors.*;
import org.codehaus.jparsec.functors.Map;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by babar on 1/26/14.
 */
@SuppressWarnings("deprecation")
public class DateParserFactory {
	static final String[] monthsArray = new String[]{"january", "february", "march", "april",
        "may", "june", "july", "august", "september", "october", "november", "december"};

	static final String[] months = addArrays(monthsArray, monthAbbreviation(monthsArray));
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy");
	
	static final String[] monthDaySuffixArray = new String[]{"st", "nd", "rd", "th"};
	
	static final String[] weekdays = new String[]{"monday", "tuesday", "wednesday",
	        "thursday", "friday", "saturday", "sunday"};
	static final String[] emptyStringArray = new String[0];
	
	static final String[] dateQueryKeywords = new String[]{"today", "yesterday", "days", "week", "weeks", "month", "months", "year", "years",
	                "ago", "last", "next", "before", "after", "prior", "to", "between", "and"};
	
	static final MonthPattern monthDayPattern = new MonthPattern();
	
	static final String YEAR = "yearFragment";
	
	static YearPattern yearPattern = new YearPattern();
	
	public static final String MONTH_DAY = "monthDayFragment";
	
	static Parser<Date> dateParser(Terminals terminals) {
	
	    Parser<Token> dateDelimiter = Parsers.or(terminals.token(","),terminals.token(":"));
	
	    final Parser<DateBuilder> monthParser = terminals.token(months).map(new Map<org.codehaus.jparsec.Token, DateBuilder>() {
	        @Override
	        public DateBuilder map(org.codehaus.jparsec.Token token) {
	            Tokens.Fragment fragment = (Tokens.Fragment) token.value();
	
	            SimpleDateFormat monthParser = new SimpleDateFormat("MMM");
	
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(monthParser.parse(fragment.text(), new ParsePosition(0)));
	
	            return new DateBuilder(null, calendar.get(Calendar.MONTH), null);
	        }
	    });
	
	    final Parser<DateBuilder> weekdayParser = terminals.token(weekdays).map(new Map<org.codehaus.jparsec.Token, DateBuilder>() {
	        @Override
	        public DateBuilder map(org.codehaus.jparsec.Token token) {
	            Tokens.Fragment fragment = (Tokens.Fragment) token.value();
	
	            Calendar parsedCalendar = Calendar.getInstance();
	            SimpleDateFormat dayParser = new SimpleDateFormat("EEE");
	
	            parsedCalendar.setTime(dayParser.parse(fragment.text(), new ParsePosition(0)));
	
	            return new DateBuilder(new DayField(true, parsedCalendar.get(Calendar.DAY_OF_WEEK)), null, null);
	        }
	    });
	
	    final Parser<DateBuilder> ordinalMonthDayParser = Parsers.sequence(Terminals.fragment(MONTH_DAY),
	            terminals.token(monthDaySuffixArray), new Map2<String, Token, DateBuilder>() {
	        @Override
	        public DateBuilder map(String numberText, Token token2) {
	
	            final int number = Integer.parseInt(numberText);
	
	            if(number > 0 && number <=31 && ((Tokens.Fragment) token2.value()).text().equals(monthSuffix(number)))
	                return new DateBuilder(new DayField(false,number), null, null);
	
	            return null;
	        }
	    });
	
	    final Parser<DateBuilder> monthDayParser = Parsers.or(
	            ordinalMonthDayParser,
	            Terminals.fragment(MONTH_DAY).map(new Map<String, DateBuilder>() {
	                @Override
	                public DateBuilder map(String monthDay) {
	                    return new DateBuilder(new DayField(false, Integer.parseInt(monthDay)), null, null);
	                }
	            }));
	
	    final Parser<DateBuilder> yearParser = Terminals.fragment(YEAR).map(new Map<String, DateBuilder>() {
	        @Override
	        public DateBuilder map(String year) {
	
	            int yearNumber = Integer.parseInt(year);
	
	            if(yearNumber < 1000)
	                yearNumber +=2000;
	
	            return new DateBuilder(null, null, yearNumber);
	        }
	    });
	
	    return Parsers.or(
	            multiFormatDateParser(monthParser, monthDayParser, yearParser, dateDelimiter),
	            multiFormatDateParser(monthParser, monthDayParser, dateDelimiter),
	            multiFormatDateParser(monthParser, yearParser, dateDelimiter),
	            dateIntervalParser(monthParser), dateIntervalParser(weekdayParser),
	            dateIntervalParser(ordinalMonthDayParser), dateIntervalParser(yearParser));
	}
	
	static Parser<String> numericMonthDayParser() {
	    return Scanners.pattern(monthDayPattern, MONTH_DAY).source();
	}
	
	static Parser<String> yearParser() {
	    return Scanners.pattern(yearPattern, YEAR).source();
	}
	
	static Parser<Date> numericDateParser(Terminals terminals, final DateComponentOrder dateComponentOrder) {
	
	    Map3<String, String, String, Date> map = new Map3<String, String, String, Date>() {
	        @Override
	        public Date map(String s, String s1, String s2) {
	
	            switch (dateComponentOrder) {
	                case BigEndian:
	                    return makeDate(Integer.parseInt(s2), Integer.parseInt(s1), Integer.parseInt(s));
	                case SmallEndian:
	                    return makeDate(Integer.parseInt(s), Integer.parseInt(s1), Integer.parseInt(s2));
	                case MiddleEndian:
	                    return makeDate(Integer.parseInt(s1), Integer.parseInt(s), Integer.parseInt(s2));
	            }
	
	            throw new IllegalArgumentException();
	        }
	    };
	
	    return numericDateParser(terminals, map);
	}
	
	static Parser<Iterable<Date>> numericDateParser(Terminals terminals) {
	
	    Map3<String, String, String, Iterable<Date>> map = new Map3<String, String, String, Iterable<Date>>() {
	        @Override
	        public Iterable<Date> map(final String s,final String s1,final String s2) {
	
	            List<Integer> numbers = Arrays.asList(
	                    Integer.parseInt(s), Integer.parseInt(s1), Integer.parseInt(s2));
	
	            List<Integer> monthCandidates = numbers.subList(0,2);
	
	            List<Integer> yearCandidates = Arrays.asList(numbers.get(0), numbers.get(2));
	
	            final Predicate<Integer> isMonthPredicate = new Predicate<Integer>() {
	                @Override
	                public boolean apply(Integer value) {
	                    return value <= 12;
	                }
	            };
	
	            final Predicate<Integer> isMonthDayPredicate = new Predicate<Integer>() {
	                @Override
	                public boolean apply(Integer value) {
	                    return value <= 31;
	                }
	            };
	
	            Set<Date> dateSet = new HashSet<Date>();
	
	            for (int month : find(monthCandidates, isMonthPredicate))
	                for (int day : find(remove(numbers, month, null), isMonthDayPredicate))
	                    for (int year : remove(yearCandidates, month, day))
	                        dateSet.add(makeDate(day, month, year));
	
	            return dateSet;
	        }
	    };
	
	    return numericDateParser(terminals, map);
	}
	
	private static <T> Parser<T> numericDateParser(Terminals terminals, Map3<String, String, String, T> map) {
	    Parser<Token> numericDateDelimiter = Parsers.or(terminals.token(","), terminals.token("\\"), terminals.token("/"),
	            terminals.token("-"), terminals.token(":"), terminals.token("."));
	
	    return Parsers.sequence(
	            Terminals.IntegerLiteral.PARSER.followedBy(numericDateDelimiter.optional()),
	            Terminals.IntegerLiteral.PARSER.followedBy(numericDateDelimiter.optional()),
	            Terminals.IntegerLiteral.PARSER, map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Parser numericMonthYearDateParser(Terminals terminals) {
	    Parser<Token> numericDateDelimiter = Parsers.or(terminals.token(","), terminals.token("\\"), terminals.token("/"),
	            terminals.token("-"), terminals.token(":"), terminals.token("."));
	
	    return Parsers.sequence(
	            Terminals.IntegerLiteral.PARSER.followedBy(numericDateDelimiter.optional()),
	            Terminals.IntegerLiteral.PARSER.followedBy(numericDateDelimiter.optional()),
	            new Map2<String, String, Date>() {
	                @Override
	                public Date map(String s, String s2) {
	                    return makeDate(1, Integer.parseInt(s), Integer.parseInt(s2));
	                }
	            });
	}
	
	private static List<Integer> find(List<Integer> values, Predicate<Integer> predicate) {
	
	    List<Integer> foundValues = new ArrayList<Integer>();
	
	    for (int val : values)
	        if (predicate.apply(val)) foundValues.add(val);
	
	    return foundValues;
	}
	
	private static List<Integer> remove(List<Integer> values, Integer value, Integer value1) {
	
	    List<Integer> newList = new ArrayList<Integer>();
	
	    int index = values.indexOf(value);
	    int index1 = values.indexOf(value1);
	
	    for (int i = 0; i < values.size(); i++)
	        if (i != index && i != index1) newList.add(values.get(i));
	
	    return newList;
	}
	
	private static Date makeDate(int day, int month, int year) {
	    final Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_MONTH, day);
	
	    if(year < 100) {
	        final Calendar calendar1 = Calendar.getInstance();
	        calendar1.setTime(new Date());
	
	        int thisYear = calendar1.get(Calendar.YEAR);
	        int thisCentury = thisYear / 100;
	
	        if(year > (thisYear - thisCentury))
	            year = (thisCentury -1)*100 + year;
	        else year = thisCentury * 100 + year;
	    }
	
	    calendar.set(Calendar.YEAR, year);
	    calendar.set(Calendar.MONTH, month-1);
	
	    return calendar.getTime();
	}
	
	private static Parser<Date> multiFormatDateParser(Parser<DateBuilder> monthParser, Parser<DateBuilder> dayParser,
	                                                  Parser<DateBuilder> yearParser, Parser<Token> dateDelimiter) {
	
	    List<Parser<DateBuilder>> dateIntervalParsers = Arrays.asList(
	            monthParser, dayParser, yearParser);
	
	    LinkedList<Parser<Tuple3<DateBuilder, DateBuilder, DateBuilder>>> multiFormatDateParsers =
	            new LinkedList<Parser<Tuple3<DateBuilder, DateBuilder, DateBuilder>>>();
	
	    for (int i = 0; i < 3; i++)
	        for (int j = 0; j < 3; j++)
	            for (int k = 0; k < 3; k++)
	                if (i != j && i != k && j != k)
	                    multiFormatDateParsers.add(Parsers.tuple(
	                            dateIntervalParsers.get(i).followedBy(dateDelimiter.optional()),
	                            dateIntervalParsers.get(j).followedBy(dateDelimiter.optional()),
	                            dateIntervalParsers.get(k)));
	
	
	    return Parsers.or(multiFormatDateParsers).map(new Map<Tuple3<DateBuilder, DateBuilder, DateBuilder>, Date>() {
	        @Override
	        public Date map(Tuple3<DateBuilder, DateBuilder, DateBuilder> dateBuilderTriple) {
	
	            Calendar calendar = getDefaultCalendar();
	
	            DateField dateField = filterNulls(Arrays.asList(getDateField(dateBuilderTriple.a),
	                    getDateField(dateBuilderTriple.b), getDateField(dateBuilderTriple.c)));
	
	            if(dateField == null)
	                return null;
	
	            dateField.setFieldOnCalendar(calendar);
	
	            calendar.set(Calendar.MONTH, filterNulls(new Integer[]{ getMonthField(dateBuilderTriple.a),
	                    getMonthField(dateBuilderTriple.b), getMonthField(dateBuilderTriple.c)
	            }));
	
	            calendar.set(Calendar.YEAR, filterNulls(new Integer[]{ getYearField(dateBuilderTriple.a),
	                    getYearField(dateBuilderTriple.b), getYearField(dateBuilderTriple.c)
	            }));
	
	            return calendar.getTime();
	        }
	    });
	}
	
	private static Parser<Date> multiFormatDateParser(Parser<DateBuilder> parser, Parser<DateBuilder> parser1,
	                                                  Parser<Token> dateDelimiter) {
	
	    List<Parser<DateBuilder>> dateIntervalParsers = Arrays.asList(
	             parser, parser1);
	
	    LinkedList<Parser<Pair<DateBuilder, DateBuilder>>> multiFormatDateParsers =
	            new LinkedList<Parser<Pair<DateBuilder, DateBuilder>>>();
	
	    for (int i = 0; i < 2; i++)
	        for (int j = 0; j < 2; j++)
	            if (i != j)
	                multiFormatDateParsers.add(Parsers.pair(
	                        dateIntervalParsers.get(i).followedBy(dateDelimiter.optional()),
	                        dateIntervalParsers.get(j)));
	
	
	    return Parsers.or(multiFormatDateParsers).map(new Map<Pair<DateBuilder, DateBuilder>, Date>() {
	        @Override
	        public Date map(Pair<DateBuilder, DateBuilder> dateBuilderPair) {
	
	            final Calendar calendar = getDefaultCalendar();
	
	            DateField dayFieldA = getDateField(dateBuilderPair.a);
	            DateField dayFieldB = getDateField(dateBuilderPair.b);
	
	            tryExtractingNonNullValueFromList(Arrays.asList(dayFieldA, dayFieldB))
	                    .useValueIfAny(new ValueGetter<DateField>() {
	                        @Override
	                        public void getValue(DateField value) {
	                            value.setFieldOnCalendar(calendar);
	                        }
	                    });
	
	            tryExtractIntegerFromArray(new Integer[]{ getMonthField(dateBuilderPair.a),
	                     getMonthField(dateBuilderPair.b)
	            }).useValueIfAny(new ValueGetter<Integer>() {
	                @Override
	                public void getValue(Integer value) {
	                    calendar.set(Calendar.MONTH, value);
	                }
	            });
	
	            tryExtractIntegerFromArray(new Integer[]{getYearField(dateBuilderPair.a),
	                    getYearField(dateBuilderPair.b)
	            }).useValueIfAny(new ValueGetter<Integer>() {
	                @Override
	                public void getValue(Integer value) {
	                    calendar.set(Calendar.YEAR, value);
	                }
	            });
	
	            return calendar.getTime();
	        }
	    });
	}
	
	private static Calendar getDefaultCalendar() {
	    final Calendar calendar = Calendar.getInstance();
	
	    calendar.set(Calendar.MONTH, Calendar.JANUARY);
	    calendar.set(Calendar.DAY_OF_MONTH,1);
	    return calendar;
	}
	
	private static DateField getDateField(DateBuilder dateBuilder) {
	    return dateBuilder == null ? null : dateBuilder.getDayField();
	}
	
	private static Integer getMonthField(DateBuilder dateBuilder) {
	    return dateBuilder == null ? null : dateBuilder.getMonth();
	}
	
	private static Integer getYearField(DateBuilder dateBuilder) {
	    return dateBuilder == null ? null : dateBuilder.getYear();
	}
	
	private static Parser<Date> dateIntervalParser(Parser<DateBuilder> parser) {
	
	    return parser.map(new Map<DateBuilder, Date>() {
	        @Override
	        public Date map(DateBuilder dateBuilder) {
	
	            final Calendar calendar = Calendar.getInstance();
	
	            DateField dayField =  dateBuilder.getDayField() == null ? new DayField(false, 1) : null;
	            Integer month = dateBuilder.getMonth() == null ? Calendar.JANUARY : null;
	            Integer year = dateBuilder.getYear();
	
	            if (dayField == null && month == null && year == null)
	                return null;
	
	            if (dayField != null)
	                dayField.setFieldOnCalendar(calendar);
	
	            if (month != null)
	                calendar.set(Calendar.MONTH, month);
	
	            if (year != null)
	                calendar.set(Calendar.YEAR, year);
	
	            return calendar.getTime();
	        }
	    });
	}
	
	private static TryResult<Integer> tryExtractIntegerFromArray(Integer[] integers) {
	    Integer val = null;
	
	    for (Integer i : integers)
	        if (i != null) {
	            assert (val == null);
	            val = i;
	        }
	
	    return new TryResult<Integer>(val);
	}
	
	private static <E> TryResult<E> tryExtractingNonNullValueFromList(List<E> values) {
	    E val = null;
	
	    for (E i : values)
	        if (i != null) {
	            assert (val == null);
	            val = i;
	        }
	
	    return new TryResult<E>(val);
	}
	
	private static int filterNulls(Integer[] integers) {
	
	    final TryResult<Integer> tryResult = tryExtractIntegerFromArray(integers);
	
	    final Integer val = tryResult.getValueOrElse((Integer) null);
	    assert (val != null);
	    return val;
	}
	
	private static <E> E filterNulls(List<E> objects) {
	
	    for (E object : objects)
	        if(object != null)
	            return object;
	
	    return null;
	}
	
	private static String[] monthAbbreviation(String[] monthsArray) {
	    String[] abbreviations = new String[monthsArray.length];
	
	    for (int i = 0; i < monthsArray.length; i++)
	        abbreviations[i] = monthsArray[i].substring(0, 3);
	
	    return abbreviations;
	}
	
	static String[] addArrays(String[]... stringArrays) {
	
	    int length = 0;
	
	    for (String[] stringArray : stringArrays)
	        length += stringArray.length;
	
	    String[] result = new String[length];
	
	    length = 0;
	
	    for (String[] stringArray : stringArrays) {
	        System.arraycopy(stringArray, 0, result, length, stringArray.length);
	        length += stringArray.length;
	    }
	
	    return result;
	}
	
	public static String monthSuffix(int i) {
	
	    final String[] suffixArray = new String[]{"th", "st", "nd",
	            "rd", "th", "th", "th", "th", "th", "th"};
	
	    switch (i % 100) {
	        case 11:
	        case 12:
	        case 13:
	            return "th";
	        default:
	            return suffixArray[i % 10];
	    }
	}
	
	private static interface Predicate<T> {
	
	    boolean apply(T value);
	}
	
	private static final class DateBuilder {
	
	    private final DayField dayField;
	
	    private final Integer month;
	
	    private final Integer year;
	
	    private DateBuilder(DayField dayField, Integer month, Integer year) {
	        this.dayField = dayField;
	        this.month = month;
	        this.year = year;
	    }
	
	    public DateField getDayField() {
	        return dayField;
	    }
	
	    public Integer getMonth() {
	        return month;
	    }
	
	    @SuppressWarnings("unused")
		public DateBuilder setMonth(Integer month) {
	        return new DateBuilder(null, month, null);
	    }
	
	    public Integer getYear() {
	        return year;
	    }
	
	    @SuppressWarnings("unused")
		public DateBuilder setYear(Integer year) {
	        return new DateBuilder(null, null, year);
	    }
	}
	
	private static abstract class DateField{
	
	    private final int id;
	    private final int value;
	
	    private DateField(int id, int value) {
	        this.id = id;
	        this.value = value;
	    }
	
	    public void setFieldOnCalendar(Calendar calendar){
	        calendar.set(id,value);
	    }
	}
	
	private static final class DayField extends DateField{
	
	    private DayField(boolean isDayOfWeek, int value) {
	        super(isDayOfWeek ? Calendar.DAY_OF_WEEK:Calendar.DAY_OF_MONTH, value);
	    }
	}
}
