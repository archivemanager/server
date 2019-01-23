package org.archivemanager.services.search.parsing.date;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Token;
import org.archivemanager.services.search.dictionary.CompoundDateDefinition;
import org.archivemanager.services.search.dictionary.DateRangeDefinition;
import org.archivemanager.util.NumberUtility;
import org.joda.time.DateTime;


public class DateParsingUtility {
	//private static final Logger log = LoggerFactory.getLogger(LuceneDateParser.class);
	protected Date startDate;
	
	
	public DateParsingUtility() {
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, -200);
		startDate = cal.getTime();
	}
	
	public String getDateClause(Token token1, Token token2) {
		StringWriter writer = new StringWriter();
		String dateStr = parseDateYMD(token1.getName());
		if(dateStr.length() == 0) dateStr = parseDateYMD(token2.getName());
		if(token2.getDefinitions().size() > 0 && token2.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			CompoundDateDefinition def = (CompoundDateDefinition)token2.getDefinitions().get(0);			
			for(Clause clause : def.getClauses()) {
				writer.append("(");
				for(Property parm : clause.getProperties()) {
					if(parm.getQName().getLocalName().equals("date")) writer.append("+"+parm.getValue()+":"+dateStr+" ");
					else writer.append("+"+parm.getQName().getLocalName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
		} 		
				
		return writer.toString().trim();//name+":"+value;
	}
	public String getDateClause(Token token) {
		StringWriter writer = new StringWriter();		
		String name = token.getName();
		String value = token.getValue();		
		if(token.getDefinitions().size() > 0 && token.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			CompoundDateDefinition def = (CompoundDateDefinition)token.getDefinitions().get(0);			
			String dateStr = "";
			for(Clause clause : def.getClauses()) {
				writer.append("(");
				for(Property parm : clause.getProperties()) {
					if(parm.getQName().getLocalName().equals("date")) writer.append("+"+parm.getValue()+":"+dateStr+" ");
					else writer.append("+"+parm.getQName().getLocalName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
		} else {
			if(token.getDefinitions().size() > 0) {
				DateRangeDefinition def = (DateRangeDefinition)token.getDefinitions().get(0);
				if(def.getDataType() == DateRangeDefinition.TYPE_EPOCH)
					writer.append(value+":"+parseDateEPOCH(name));
				else
					writer.append(value+":"+parseDateYMD(name));
			} else writer.append(value+":"+parseDateYMD(name));		
		}
				
		return writer.toString().trim();//name+":"+value;
	}
	protected String parseDateEPOCH(String name) {
		DateTime current = new DateTime();
		if(name.equals("today")) {
			DateTime start = current.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
			//log.info("date parsed to ["+start.toString()+" TO "+current.toString()+"]");
			return "["+start.getMillis()+" TO "+current.getMillis()+"]";
		} else if(name.equals("yesterday")) {
			DateTime start = new DateTime().minusDays(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
			DateTime end = new DateTime().minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
			//log.info("date parsed to ["+start.toString()+" TO "+end.toString()+"]");
			return "["+start.getMillis()+" TO "+end.getMillis()+"]";
		} else if(name.equals("year to date")) {
			DateTime start = current.withDayOfYear(1);
			return "["+start.getMillis()+" TO "+current.getMillis()+"]";
		} else if(name.equals("all time")) {			
			return "["+startDate.getTime()+" TO "+current.getMillis()+"]";
		} else if(name.startsWith("last") && name.endsWith("months")) {
			String[] parts = name.split(" ");
			if(parts.length == 3 && NumberUtility.isInteger(parts[1])) {
				int roll = Integer.valueOf(parts[1]);
				DateTime start = current.minusDays(roll);
				return "["+start.getMillis()+" TO "+current.getMillis()+"]";
			}
		}
		return "";
	}
	protected String parseDateYMD(String name) {
		DateTime current = new DateTime();
		DateTime past = new DateTime();
		if(name.equals("year to date")) return "["+current.getYear()+"0101 TO "+DateTools.dateToString(current.toDate(), Resolution.DAY)+"]";
		else if(name.equals("all time")) return "["+DateTools.dateToString(startDate, Resolution.DAY)+" TO "+DateTools.dateToString(current.toDate(), Resolution.DAY)+"]";
		else if(name.startsWith("last") && name.endsWith("months")) {
			String[] parts = name.split(" ");
			if(parts.length == 3 && NumberUtility.isInteger(parts[1])) {
				int roll = Integer.valueOf(parts[1]);
				return "["+DateTools.dateToString(past.minusMonths(roll).toDate(), Resolution.DAY)+" TO "+DateTools.dateToString(current.toDate(), Resolution.DAY)+"]";
			}
		}
		return "";
	}
}
