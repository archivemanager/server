package org.archivemanager.services.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.archivemanager.data.io.FastStringBuffer;



public abstract class JobSupport implements Job {
	private static final long serialVersionUID = 4979130563698205091L;
	private Long id;
	private String uid;
	private String group;
	private Map<String,String> properties = new HashMap<String,String>();
	private Map<String,Object> data = new HashMap<String,Object>();
	private List<Status> statusList = new ArrayList<Status>();
	private String lastMessage;
	private List<String> errors = new ArrayList<String>();
	private List<String> warnings = new ArrayList<String>();
	private List<Processor> processors = new ArrayList<Processor>();
	private boolean complete = false;
	volatile private boolean interrupted;	
	private long startTime;
	private long endTime;
	private int docsProcessed;
	private int dirsProcessed;
	private ExecutionContext context;
	
	private Random random = new Random();
	
	public JobSupport() {
		this.id = random.nextLong();
		this.uid = java.util.UUID.randomUUID().toString();
	}
		
	@Override
	public void execute() {
		startTime = System.currentTimeMillis();
	}
		
	public void error(String msg) {
		errors.add(msg);
	}
	public void error(String msg, Throwable t) {
		errors.add(msg+" - "+t);
	}
	public int getErrorCount() {
		return errors.size();
	}
	public void warn(String msg) {
		warnings.add(msg);
	}
	public int getWarningCount() {
		return warnings.size();
	}
	public boolean getInterrupted() {
		return interrupted;
	}
	public List<Processor> getProcessors() {
		return processors;
	}
	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

	protected Status createStatus() {
		Status status = new Status();		
		status.setId(random.nextLong());
		status.setUid(java.util.UUID.randomUUID().toString());
		try {
			//status.addProperty(new QName("org.openapps_system_1.0","fire_time"), String.valueOf(getProperty("org.openapps_system_1.0","fire_time")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		statusList.add(status);
		return status;
	}
	public Map<String,Object> getData() {
		return data;
	}	
	
	protected Serializable getProperty(String localName) {
		return properties.get(localName);
	}
	protected void setProperty(String localName, Serializable value) {
		try {
			properties.put(localName, String.valueOf(value));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public String getLastMessage() {
		if(lastMessage == null) return "";
		return lastMessage + " (" + getFormattedElapsedTime() + ")";
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public List<Status> getStatus() {
		return statusList;
	}
	public void setStatus(List<Status> status) {
		this.statusList = status;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public List<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	public int getDocsProcessed() {
		return docsProcessed;
	}
	public void incrementDocsProcessed() {
		docsProcessed++;
	}
	public void setDocsProcessed(int docsProcessed) {
		this.docsProcessed = docsProcessed;
	}
	public int getDirsProcessed() {
		return dirsProcessed;
	}
	public void incrementDirsProcessed() {
		dirsProcessed++;
	}
	public void setDirsProcessed(int dirsProcessed) {
		this.dirsProcessed = dirsProcessed;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public ExecutionContext getExecutionContext() {
		return context;
	}
	public void setExecutionContext(ExecutionContext context) {
		this.context = context;
	}

	/**
	 * Return a string which converts the milliseconds value in the elapsed
	 * parameter to day/hours/minutes/seconds.
	 */
	public String getFormattedElapsedTime() {
		float elapsed = System.currentTimeMillis() - startTime;
		FastStringBuffer buf = new FastStringBuffer();
		elapsed = elapsed / (24 * 60 * 60 * 1000);
		int days = (int) elapsed;
		elapsed -= days;
		elapsed *= 24;
		int hours = (int) elapsed;
		elapsed -= hours;
		elapsed *= 60;
		int minutes = (int) elapsed;
		elapsed -= minutes;
		elapsed *= 60;
		int seconds = (int) elapsed;
		//elapsed -= seconds;
		//elapsed *= 1000;
		//int milliseconds = (int) elapsed;
		if (days > 0) buf.append(days + " days ");
		if (hours > 0) buf.append(hours + " hours ");
		if (minutes > 0) buf.append(minutes + " minutes ");
		if (seconds > 0) buf.append(seconds + " seconds ");
		//if (milliseconds > 0) buf.append(milliseconds + " ms ");
		if (buf.size() == 0) buf.append("1 ms");
		return buf.toString();
	}
}
