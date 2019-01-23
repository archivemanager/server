package org.archivemanager.services.entity;

public interface EntityCleaningService {
	public static final int MERGE_FIRST_TO_LAST = 1;
	public static final int MERGE_LAST_TO_FIRST = 2;
	
	
	void merge(Long[] ids, int mergeType); 
	
}
