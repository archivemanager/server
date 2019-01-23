package org.archivemanager.data;
import java.util.Comparator;

import org.archivemanager.models.repository.Category;



public class CategorySorter implements Comparator<Category> {
	
	
	public int compare(Category e1, Category e2) {
		String field1 = removeTags(e1.getName());
		String field2 = removeTags(e2.getName());
		for(int i=0; i < field1.length(); i++) {
			if(field2.length() > i && field1.charAt(i) != field2.charAt(i)) {
				if(Character.isLetter(field1.charAt(i)) && Character.isLetter(field2.charAt(i)))
					return Character.valueOf(field1.charAt(i)).compareTo(Character.valueOf(field2.charAt(i)));
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
						return 1;
					else return -1;
				}
			}
		}
		return 0;
	}
	protected String removeTags(String in) {
		if(in == null) return "";
    	return in.replaceAll("\\<.*?>","");
	}
}