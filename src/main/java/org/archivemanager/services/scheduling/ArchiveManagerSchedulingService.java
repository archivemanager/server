package org.archivemanager.services.scheduling;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.services.cache.TimedCache;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.search.indexing.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class ArchiveManagerSchedulingService implements SchedulingService {
	@Autowired private PropertyConfiguration properties;
	
	private TimedCache<String,Job> jobCache = new TimedCache<String,Job>(60);
	private PoolWorker[] threads;
	private ScheduleWorker scheduler;
	private List<Job> schedule = new ArrayList<Job>();
	private LinkedList<Job> queue;	
	
	final static public String GROUP_NAME = "openapps";
	

	public void shutdown() {
		
	}
	
	@PostConstruct
	public void initialize() {
		queue = new LinkedList<Job>();
        threads = new PoolWorker[properties.getSchedulingThreads()];
        for (int i=0; i<properties.getSchedulingThreads(); i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
        scheduler = new ScheduleWorker();
        scheduler.start();
	}
	@Override
	public void run(Job job) {
		try {
			if(job.getExecutionContext() == null) {
				job.setExecutionContext(new StandardExecutionContext());
			}
			synchronized(queue) {
				if(!queue.contains(job)) {
	        		queue.addLast(job);
	        		jobCache.put(job.getUid(), job);
	        	}
				queue.notify();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void runAndWait(Job job) {
		job.getExecutionContext().setFireTime(System.currentTimeMillis());
        job.execute();
	}
	public void run(Job job, int count, Period period) {
		StandardExecutionContext ctx = new StandardExecutionContext();
		ctx.setJob(job);
		ctx.setCount(count);
		ctx.setPeriod(period);
		synchronized(schedule) {
			schedule.add(job);
		}
	}
	@Override
	public Job getJob(String jobid) {
		Job job = jobCache.get(jobid);
		return job;
	}
	@Override
	public List<Job> getJobs(String node) {
		List<Job> list = new ArrayList<Job>();
		for(String key : jobCache.keySet()) {
			Job job = jobCache.get(key);
			if(job != null && job.getId() != null) {
				if(job.getId().toString().equals(node)) list.add(job);
			}
		}
		return list;
	}
	@Override
	public List<Job> getJobs() {
		List<Job> list = new ArrayList<Job>();
		for(String key : jobCache.keySet()) {
			Job job = jobCache.get(key);
			if(job.getId() != null) {
				list.add(job);
			}
		}
		return list;
	}
	@Override
	public void updateJob(String id, String message) {
		Job job = getJob(id);
		job.setLastMessage(message);
	}
	
	@Override
	public void interruptJob(String id) throws SchedulingException {
		
	}
	@Override
	public boolean startJob(String id) throws SchedulingException {
		return false;
	}
	@Override
	public void removeJob(String id) throws SchedulingException {
		
	}
	
	private class PoolWorker extends Thread {
        public void run() {
        	Job job = null;
        	while(true) {
        		synchronized(queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();                            
                        } catch (InterruptedException ignored) {}
                    } 
                    job = queue.removeFirst();
        		}        		
                job.getExecutionContext().setFireTime(System.currentTimeMillis());
                job.execute();
        	}
        }
	}
	private class ScheduleWorker extends Thread {
		public void run() {
			while(true) {
				synchronized(schedule) {
					try {
						schedule.wait(1000 * 60); //every minute
	                    for(Job job : schedule) {
	                    	ExecutionContext ctx = job.getExecutionContext();
	                    	if(ctx.getNextFireTime() <= System.currentTimeMillis()) {
	                    		job.getExecutionContext().setFireTime(System.currentTimeMillis());
	                    		job.execute();
	                    	}
	                    }
					} catch (InterruptedException ignored) {}	
				}
			}
		}
	}
		
}