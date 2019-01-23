package org.archivemanager.services.entity;
import java.io.Serializable;
import java.util.Comparator;

import org.archivemanager.data.Sort;
import org.archivemanager.util.NumberUtility;


public class EntitySorter implements Comparator<Entity>, Serializable {
	private static final long serialVersionUID = 6893881851620745675L;
	private Sort sort;
	
	
	public EntitySorter(Sort sort) {
		this.sort = sort;
	}
	
	public int compare(Entity e1, Entity e2) {
		int response = 0;
		String field1 = null;
		String field2 = null;
		try {
			if(e1 == null || e1 == null) response = 0;
			Property property1 = null;
			Property property2 = null;
			property1 = sort.getDirection().equals("asc") ? e2.getProperty(sort.getField()) : e1.getProperty(sort.getField());
			property2 = sort.getDirection().equals("asc") ? e1.getProperty(sort.getField()) : e2.getProperty(sort.getField());
			if(property1 != null && property2 != null) {
				field1 = removeTags(String.valueOf(property1));
				field2 = removeTags(String.valueOf(property2));
				
				return compare(field1, field2);//field1.compareToIgnoreCase(field2);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	public int compare(String s1, String s2) {
    	if ((s1 == null) || (s2 == null)) 
    	{
    		return 0;
    	}
        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();
        while (thisMarker < s1Length && thatMarker < s2Length)
        {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result = 0;
            if (NumberUtility.isDigit(thisChunk.charAt(0)) && NumberUtility.isDigit(thatChunk.charAt(0)))
            {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0)
                {
                    for (int i = 0; i < thisChunkLength; i++)
                    {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0)
                        {
                            return result;
                        }
                    }
                }
            } 
            else
            {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0)
                return result;
        }
        return s1Length - s2Length;
    }
	/*
	public int compare(Entity e1, Entity e2) {
		int response = 0;
		String field1 = null;
		String field2 = null;
		try {
			if(e1 == null || e1 == null) response = 0;
			Property property1 = null;
			Property property2 = null;
			if(sort.getField().equals(SystemModel.NAME.toString())) {
				property1 = sort.isReverse() ? new Property(SystemModel.NAME, "0", e2.getName()) : new Property(SystemModel.NAME, "0", e1.getName());
				property2 = sort.isReverse() ? new Property(SystemModel.NAME, "0", e1.getName()) : new Property(SystemModel.NAME, "0", e2.getName());
			} else {
				property1 = sort.isReverse() ? e2.getProperty(sort.getField()) : e1.getProperty(sort.getField());
				property2 = sort.isReverse() ? e1.getProperty(sort.getField()) : e2.getProperty(sort.getField());
			}
			if(property1 != null && property2 != null) {
				if(sort.getType() == null) sort.setType(property1.getType());
				field1 = removeTags(String.valueOf(property1));
				field2 = removeTags(String.valueOf(property2));
				if(sort.getType().equals(ModelField.TYPE_DATE)) {
					try {
						response = dateFormatter.parse(field1).compareTo(dateFormatter.parse(field2));
					} catch(Exception e) {}
				} else if(sort.getType().equals(ModelField.TYPE_INTEGER) || sort.getType().equals(Sort.DOUBLE) || sort.getType().equals(Sort.LONG)) {
					response = Integer.valueOf(field1).compareTo(Integer.valueOf(field2));
				} else {
					for(int i=0; i < field1.length(); i++) {
						if(field2.length() > i && field1.charAt(i) != field2.charAt(i)) {
							if(Character.isLetter(field1.charAt(i)) && Character.isLetter(field2.charAt(i)))
								response = Character.valueOf(field1.charAt(i)).compareTo(Character.valueOf(field2.charAt(i)));
							else if(Character.isSpace(field1.charAt(i)) && !Character.isSpace(field2.charAt(i)))
								response = -1;
							else if(Character.isSpace(field2.charAt(i)) && !Character.isSpace(field1.charAt(i)))
								response = 1;
							else if(Character.isDigit(field1.charAt(i)) && Character.isDigit(field2.charAt(i))) {
								StringBuffer digit1 = new StringBuffer();
								StringBuffer digit2 = new StringBuffer();
								int index = i;
								while(field1.length() > index && Character.isDigit(field1.charAt(index))) {
									digit1.append(field1.charAt(index));
									index++;
								}
								index = i;
								while(field2.length() > index && Character.isDigit(field2.charAt(index))) {
									digit2.append(field2.charAt(index));
									index++;
								}
								if(Integer.valueOf(digit1.toString()) > Integer.valueOf(digit2.toString()))
									response = 1;
								else response = -1;
							}
						}
					}
					response = field1.compareTo(field2);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	*/
	protected String removeTags(String in) {
		if(in == null) return "";
    	return in.replaceAll("[^A-Za-z0-9]","");
	}
	/** Length of string is passed in for improved efficiency (only need to calculate it once) **/
	protected final String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (NumberUtility.isDigit(c))
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (!NumberUtility.isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        } else
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (NumberUtility.isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }
}