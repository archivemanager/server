package org.archivemanager.util;

public class NumberUtility {

	
	public static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
	}
	public static boolean isDigit(char ch) {
        return ((ch >= 48) && (ch <= 57));
    }
	public static boolean isDouble(String data) {
		try {
			Double.valueOf(data);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public static boolean isInteger(String data) {
		try {
			Integer.valueOf(data);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public static boolean isLong(String data) {
		try {
			Long.valueOf(data);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public static Long getLong(Object in) {
		if(in != null) {
			if(in instanceof Long) return (Long)in;
			else if(in instanceof String) return Long.valueOf((String)in);
			else if(in instanceof Integer) return Long.valueOf(((Integer)in).longValue());
		}
		return null;
	}
	public static String int2RomanNumeral(int n) {
		if (n >= 4000  || n < 1) {
            throw new NumberFormatException("Numbers must be in range 1-3999");
        }
        StringBuffer result = new StringBuffer(10);
        //... Start with largest value, and work toward smallest.
        for (RomanValue equiv : ROMAN_VALUE_TABLE) {
            //... Remove as many of this value as possible (maybe none).
            while (n >= equiv.intVal) {
                n -= equiv.intVal;            // Subtract value.
                result.append(equiv.romVal);  // Add roman equivalent.
            }
        }
        return result.toString();

	}
	
	final static RomanValue[] ROMAN_VALUE_TABLE = {
        new RomanValue(1000, "M"),
        new RomanValue( 900, "CM"),
        new RomanValue( 500, "D"),
        new RomanValue( 400, "CD"),
        new RomanValue( 100, "C"),
        new RomanValue(  90, "XC"),
        new RomanValue(  50, "L"),
        new RomanValue(  40, "XL"),
        new RomanValue(  10, "X"),
        new RomanValue(   9, "IX"),
        new RomanValue(   5, "V"),
        new RomanValue(   4, "IV"),
        new RomanValue(   1, "I")
    };
	private static class RomanValue {
        int    intVal;     // Integer value.
        String romVal;     // Equivalent roman numeral.
        RomanValue(int dec, String rom) {
            this.intVal = dec;
            this.romVal = rom;
        }
    }

}
