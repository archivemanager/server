package org.archivemanager.services.scheduling;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface Job extends Serializable {

	Long getId();
	String getUid();
	String getGroup();
	
	void execute();
	Map<String,Object> getData();
	List<Status> getStatus();
	boolean isComplete();
	
	void error(String msg, Throwable t);
	int getWarningCount();
	int getErrorCount();
	String getLastMessage();
	void setLastMessage(String msg);
	
	ExecutionContext getExecutionContext();
	void setExecutionContext(ExecutionContext ctx);
	
}
