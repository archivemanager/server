package org.archivemanager.services.scheduling;
import java.util.List;


public interface SchedulingService {

	void run(Job job);
	void runAndWait(Job job);
	//void run(Job job, int count, Period period);
	
	void interruptJob(String jobName) throws SchedulingException;
	boolean startJob(String jobName) throws SchedulingException;
	void removeJob(String jobName) throws SchedulingException;
	
	void updateJob(String id, String message);
	
	Job getJob(String jobid);
	//List<JobInfo> getJobs() throws SchedulingException;
	List<Job> getJobs(String node);
	List<Job> getJobs();
	
	void run(Job job, int count, Period period);
}
