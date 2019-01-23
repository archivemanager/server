package org.archivemanager.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtility {
	final static TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	private static final ThreadLocal<Calendar> TL_CAL = new ThreadLocal<Calendar>() {
	    @Override
	    protected Calendar initialValue() {
	      return Calendar.getInstance(GMT, Locale.US);
	    }
	};
	
	@SuppressWarnings("deprecation")
	public static void setToCurrentTime(Date date) {
		Date currentDate = new Date();
		date.setHours(currentDate.getHours());
		date.setMinutes(currentDate.getMinutes());
		date.setSeconds(currentDate.getSeconds());
	}
	public static String stripDayOfWeek(String in) {
		return in.replace("Mon", "").replace("Tue", "").replace("Wed", "").replace("Thu", "").replace("Fri", "").replace("Sat", "").replace("Sun", "").trim();
	}
	public static Date changeTimezone(Date date, TimeZone fromZone, TimeZone toZone) {
		Date gmtDate = new Date(date.getTime() - fromZone.getRawOffset());
		return new Date(gmtDate.getTime() + toZone.getRawOffset());
	}
	/**
	   * Converts a Date to a string suitable for indexing.
	   * 
	   * @param date the date to be converted
	   * @param resolution the desired resolution, see
	   *  {@link #round(Date, DateTools.Resolution)}
	   * @return a string in format <code>yyyyMMddHHmmssSSS</code> or shorter,
	   *  depending on <code>resolution</code>; using GMT as timezone 
	   */
	public static String dateToString(Date date, Resolution resolution) {
		  return timeToString(date.getTime(), resolution);
	}

	private static final ThreadLocal<SimpleDateFormat[]> TL_FORMATS = new ThreadLocal<SimpleDateFormat[]>() {
		@Override
		protected SimpleDateFormat[] initialValue() {
			SimpleDateFormat[] arr = new SimpleDateFormat[Resolution.MILLISECOND.formatLen+1];
		    for (Resolution resolution : Resolution.values()) {
		    	arr[resolution.formatLen] = (SimpleDateFormat)resolution.format.clone();
		    }
		    return arr;
		}
	};
		  
	  /**
	   * Converts a millisecond time to a string suitable for indexing.
	   * 
	   * @param time the date expressed as milliseconds since January 1, 1970, 00:00:00 GMT
	   * @param resolution the desired resolution, see
	   *  {@link #round(long, DateTools.Resolution)}
	   * @return a string in format <code>yyyyMMddHHmmssSSS</code> or shorter,
	   *  depending on <code>resolution</code>; using GMT as timezone
	   */
	  public static String timeToString(long time, Resolution resolution) {
	    final Date date = new Date(round(time, resolution));
	    return TL_FORMATS.get()[resolution.formatLen].format(date);
	  }
	  
	  /**
	   * Limit a date's resolution. For example, the date <code>1095767411000</code>
	   * (which represents 2004-09-21 13:50:11) will be changed to 
	   * <code>1093989600000</code> (2004-09-01 00:00:00) when using
	   * <code>Resolution.MONTH</code>.
	   * 
	   * @param resolution The desired resolution of the date to be returned
	   * @return the date with all values more precise than <code>resolution</code>
	   *  set to 0 or 1, expressed as milliseconds since January 1, 1970, 00:00:00 GMT
	   */
	  @SuppressWarnings("fallthrough")
	  public static long round(long time, Resolution resolution) {
	    final Calendar calInstance = TL_CAL.get();
	    calInstance.setTimeInMillis(time);
	    
	    switch (resolution) {
	      //NOTE: switch statement fall-through is deliberate
	      case YEAR:
	        calInstance.set(Calendar.MONTH, 0);
	      case MONTH:
	        calInstance.set(Calendar.DAY_OF_MONTH, 1);
	      case DAY:
	        calInstance.set(Calendar.HOUR_OF_DAY, 0);
	      case HOUR:
	        calInstance.set(Calendar.MINUTE, 0);
	      case MINUTE:
	        calInstance.set(Calendar.SECOND, 0);
	      case SECOND:
	        calInstance.set(Calendar.MILLISECOND, 0);
	      case MILLISECOND:
	        // don't cut off anything
	        break;
	      default:
	        throw new IllegalArgumentException("unknown resolution " + resolution);
	    }
	    return calInstance.getTimeInMillis();
	  }
	  
	  /** Specifies the time granularity. */
	  public static enum Resolution {
	    
	    YEAR(4), MONTH(6), DAY(8), HOUR(10), MINUTE(12), SECOND(14), MILLISECOND(17);

	    final int formatLen;
	    final SimpleDateFormat format;//should be cloned before use, since it's not threadsafe

	    Resolution(int formatLen) {
	      this.formatLen = formatLen;
	      // formatLen 10's place:                     11111111
	      // formatLen  1's place:            12345678901234567
	      this.format = new SimpleDateFormat("yyyyMMddHHmmssSSS".substring(0,formatLen),Locale.US);
	      this.format.setTimeZone(GMT);
	    }

	    /** this method returns the name of the resolution
	     * in lowercase (for backwards compatibility) */
	    @Override
	    public String toString() {
	      return super.toString().toLowerCase(Locale.ENGLISH);
	    }
	  }
	  public static String decodeDayOfMonth(String in) {
		  return in.replace("1st","1").replace("2nd","2").replace("3rd","3").replace("4th","4").replace("5th","5").replace("6th","6").replace("7th","7").replace("8th","8").replace("9th","9").replace("10th","10").replace("11th","11").replace("12th","12").replace("13th","13").replace("14th","14").replace("15th","15").replace("16th","16").replace("17th","17").replace("18th","18").replace("19th","19").replace("20th","20").replace("21st","21").replace("22nd","22").replace("23rd","23").replace("24th","24").replace("25th","25").replace("26th","26").replace("27th","27").replace("28th","28").replace("29th","29").replace("30th","30").replace("31st","31");
	  }
	  public static Date decodeMinutesAgo(String in) {
		  if(in.endsWith("minutes ago")) {
			  String num = in.replace(" minutes ago", "");
			  int minutes = Integer.valueOf(num);
			  Calendar now = Calendar.getInstance();
			  now.add(Calendar.MINUTE, -minutes);
			  return now.getTime();
		  }
		  return null;
	  }
	  public static Date decodeHoursAgo(String in) {
		  if(in.endsWith("hours ago")) {
			  String num = in.replace(" hours ago", "");
			  int hours = Integer.valueOf(num);
			  Calendar now = Calendar.getInstance();
			  now.add(Calendar.HOUR_OF_DAY, -hours);
			  return now.getTime();
		  }
		  return null;
	  }
	  
	  public static long getCurrentDate() {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String year = String.valueOf(cal.get(Calendar.YEAR));
			String month = String.valueOf(cal.get(Calendar.MONTH)+1);
			if(month.length() == 1) month = "0"+month;
			String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			if(day.length() == 1) day = "0"+day;
			
			String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			if(hour.length() == 1) hour = "0"+hour;
			String minute = String.valueOf(cal.get(Calendar.MINUTE));
			if(minute.length() == 1) minute = "0"+minute;
			String second = String.valueOf(cal.get(Calendar.SECOND));
			if(second.length() == 1) second = "0"+second;
			
			return Long.valueOf(year+month+day+hour+minute+second);
	}
	public static String formatDate(long date) {
		String dateStr = String.valueOf(date);
		if(dateStr.length() == 14) {
			Calendar cal = Calendar.getInstance();
			Integer year = Integer.valueOf(dateStr.substring(0, 4));
			Integer month = Integer.valueOf(dateStr.substring(4, 6));
			Integer day = Integer.valueOf(dateStr.substring(6, 8));
			Integer hour = Integer.valueOf(dateStr.substring(8, 10));
			Integer min = Integer.valueOf(dateStr.substring(10, 12));
			Integer sec = Integer.valueOf(dateStr.substring(12, 14));
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month-1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, sec);
			SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			return format.format(cal.getTime());
		}
		return dateStr;
	}
	public static String formatDate(long date, String format) {
		String dateStr = String.valueOf(date);
		if(dateStr.length() == 14) {
			Calendar cal = Calendar.getInstance();
			Integer year = Integer.valueOf(dateStr.substring(0, 4));
			Integer month = Integer.valueOf(dateStr.substring(4, 6));
			Integer day = Integer.valueOf(dateStr.substring(6, 8));
			Integer hour = Integer.valueOf(dateStr.substring(8, 10));
			Integer min = Integer.valueOf(dateStr.substring(10, 12));
			Integer sec = Integer.valueOf(dateStr.substring(12, 14));
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month-1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, sec);
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(cal.getTime());
		}
		return dateStr;
	}
}
