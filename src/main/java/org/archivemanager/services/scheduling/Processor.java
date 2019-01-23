package org.archivemanager.services.scheduling;


public abstract class Processor {
	private Job job;
	
	
	public Processor(){}
	public Processor(Job job) {
		this.job = job;
	}
	
	public Job getJob() {
		return job;
	}
}
