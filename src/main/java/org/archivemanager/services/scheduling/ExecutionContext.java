package org.archivemanager.services.scheduling;


public interface ExecutionContext {
	
    /**
     * <p>
     * If the <code>Job</code> is being re-executed because of a 'recovery'
     * situation, this method will return <code>true</code>.
     * </p>
     */
    public boolean isRecovering();
    public void setRecovering(boolean recovering);
    
    public int getRefireCount();
    public void setRefireCount(int count);
    /**
     * The actual time the trigger fired. For instance the scheduled time may
     * have been 10:00:00 but the actual fire time may have been 10:00:03 if
     * the scheduler was too busy.
     * 
     * @return Returns the fireTime.
     * @see #getScheduledFireTime()
     */
    public long getFireTime();
    public void setFireTime(long time);
        
    public long getPreviousFireTime();
    public void setPreviousFireTime(long time);
    
    public long getNextFireTime();
    public void setNextFireTime(long time);
    
    /**
     * Get the unique Id that identifies this particular firing instance of the
     * trigger that triggered this job execution.  It is unique to this 
     * JobExecutionContext instance as well.
     * 
     * @return the unique fire instance id
     * @see Scheduler#interrupt(String)
     */
    public String getFireInstanceId();
    
    /**
     * The amount of time the job ran for (in milliseconds).  The returned 
     * value will be -1 until the job has actually completed (or thrown an 
     * exception), and is therefore generally only useful to 
     * <code>JobListener</code>s and <code>TriggerListener</code>s.
     * 
     * @return Returns the jobRunTime.
     */
    public long getJobRunTime();
    public void setJobRunTime(long time);
    /**
     * Put the specified value into the context's data map with the given key.
     * Possibly useful for sharing data between listeners and jobs.
     *
     * <p>NOTE: this data is volatile - it is lost after the job execution
     * completes, and all TriggerListeners and JobListeners have been 
     * notified.</p> 
     *  
     * @param key
     * @param value
     */
    public void put(Object key, Object value);

    /**
     * Get the value with the given key from the context's data map.
     * 
     * @param key
     */
    public Object get(Object key);
}
