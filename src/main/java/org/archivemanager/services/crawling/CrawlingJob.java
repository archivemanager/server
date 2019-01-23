package org.archivemanager.services.crawling;

import java.io.Serializable;
import java.util.List;


public interface CrawlingJob extends Serializable {
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_PAUSED = 0;
	public static final int STATUS_ERROR = -1;
	
	int getStatus();
	int getFilesProcessed();
	int getDirectoriesProcessed();
	
	List<Seed> getSeeds();
	List<Seed> getErrors();
	
}
