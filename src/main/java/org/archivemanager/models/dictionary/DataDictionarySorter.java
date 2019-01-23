package org.archivemanager.models.dictionary;

import java.io.Serializable;
import java.util.Comparator;

public class DataDictionarySorter implements Comparator<DataDictionary>, Serializable {
	private static final long serialVersionUID = -6428587336087248053L;

	public int compare(DataDictionary dictionary1, DataDictionary dictionary2) {
		return dictionary1.getName().compareTo(dictionary2.getName());
	}
}
