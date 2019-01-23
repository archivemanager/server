package org.archivemanager.util;
import java.io.Serializable;


public class DataTypeUtility {

	
	public static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
	}
	@SuppressWarnings("deprecation")
	public static boolean isDate(Serializable in) {
		try {
			if(in instanceof java.util.Date || in instanceof java.sql.Date) return true;
			if(in instanceof String) {
				java.util.Date.parse((String)in);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isBoolean(Serializable in) {
		try {
			if(in instanceof Boolean) return true;
			if(in instanceof String) {
				String data = (String)in;
				if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false"))
					return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isBoolean(Object in) {
		try {
			if(in instanceof Boolean) return true;
			if(in instanceof String) {
				String data = (String)in;
				if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false"))
					return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean getBoolean(Object in) {
		try {
			if(in instanceof Boolean) return (Boolean)in;
			if(in instanceof String) {
				String data = (String)in;
				if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false"))
					return Boolean.valueOf(data);
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isDouble(Serializable data) {
		if(data instanceof Double) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Double.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isDouble(Object data) {
		if(data instanceof Double) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Double.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static Double getDouble(Object data) {
		if(data instanceof Double) return (Double)data;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return 0.0;
				return Double.valueOf((String)data);
			}
		} catch(Exception e) {
			return 0.0;
		}
		return 0.0;
	}
	public static boolean isInteger(Serializable data) {
		if(data instanceof Integer) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Integer.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isInteger(Object data) {
		if(data instanceof Integer) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Integer.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static Integer getInteger(Object data) {
		if(data instanceof Integer) return (Integer)data;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return 0;
				return Integer.valueOf((String)data);
			}
		} catch(Exception e) {
			return 0;
		}
		return 0;
	}
	public static boolean isLong(Serializable data) {
		if(data instanceof Long) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Long.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isLong(Object data) {
		if(data instanceof Long) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Long.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static Long getLong(Object data) {
		if(data instanceof Long) return (Long)data;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return 0L;
				return Long.valueOf((String)data);
			} else if(data instanceof Integer) {
				return new Long((Integer)data);
			}
		} catch(Exception e) {
			return 0L;
		}
		return 0L;
	}
	public static boolean isFloat(Serializable data) {
		if(data instanceof Float) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Float.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isFloat(Object data) {
		if(data instanceof Float) return true;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return false;
				Float.valueOf((String)data);
				return true;
			}
		} catch(Exception e) {
			return false;
		}
		return false;
	}
	public static Float getFloat(Object data) {
		if(data instanceof Float) return (Float)data;
		try {
			if(data instanceof String) {
				if(((String)data).startsWith("0")) return 0f;
				return Float.valueOf((String)data);
			}
		} catch(Exception e) {
			return 0f;
		}
		return 0f;
	}
	
	public static final int CR = 13; // <US-ASCII CR, carriage return (13)>
    public static final int LF = 10; // <US-ASCII LF, linefeed (10)>
    public static final int SP = 32; // <US-ASCII SP, space (32)>
    public static final int HT = 9;  // <US-ASCII HT, horizontal-tab (9)>
	public static boolean isWhitespace(char ch) {
        return ch == SP || ch == HT || ch == CR || ch == LF;
    }
}
