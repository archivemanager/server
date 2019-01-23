package org.archivemanager.services.scheduling;
import java.util.HashMap;
import java.util.Map;


public class StandardExecutionContext implements ExecutionContext {
	private Map<Object, Object> properties = new HashMap<Object, Object>();
	private Job job;
		
	private Period period = Period.DAY;
	private int count = 1;
	
	private String fireInstanceId;
	private long fireTime;
	private long fireTimeNext;
	private long fireTimePrev;
	private long jobRunTime;
	private int refireCount;
	private boolean recovering;
	
	
	@Override
	public String getFireInstanceId() {
		return fireInstanceId;
	}

	@Override
	public long getFireTime() {
		return fireTime;
	}

	@Override
	public long getJobRunTime() {
		return jobRunTime;
	}

	@Override
	public long getNextFireTime() {
		return fireTimeNext;
	}
	public void setNextFireTime(long fireTimeNext) {
		this.fireTimeNext = fireTimeNext;
	}
	
	@Override
	public long getPreviousFireTime() {
		return fireTimePrev;
	}
	public void setPreviousFireTime(long fireTimePrev) {
		this.fireTimePrev = fireTimePrev;
	}
	
	@Override
	public int getRefireCount() {
		return refireCount;
	}
	
	@Override
	public boolean isRecovering() {
		return recovering;
	}
	@Override
	public void setRecovering(boolean recovering) {
		this.recovering = recovering;
	}
	public void setFireInstanceId(String fireInstanceId) {
		this.fireInstanceId = fireInstanceId;
	}

	public void setFireTime(long fireTime) {
		this.fireTimePrev = this.fireTime;
		this.fireTime = fireTime;
		switch(period) {
			case MINUTE : 
				this.fireTimeNext = fireTime + (count * 1000 * 60);
				break;
			case HOUR :
				this.fireTimeNext = fireTime + (count * 1000 * 60 * 60);
				break;
			case DAY :
				this.fireTimeNext = fireTime + (count * 1000 * 60 * 60 * 24);
				break;
			case WEEK :
				this.fireTimeNext = fireTime + (count * 1000 * 60 * 60 * 24 * 7);
				break;
			case MONTH :
				this.fireTimeNext = fireTime + (count * 1000 * 60 * 60 * 24 * 30);
				break;
			case YEAR :
				this.fireTimeNext = fireTime + (count * 1000 * 60 * 60 * 24 * 356);
				break;
		}
	}

	public void setJobRunTime(long jobRunTime) {
		this.jobRunTime = jobRunTime;
	}

	public void setRefireCount(int refireCount) {
		this.refireCount = refireCount;
	}

	@Override
	public Object get(Object key) {
		return properties.get(key);
	}
	@Override
	public void put(Object key, Object value) {
		properties.put(key, value);
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

}
