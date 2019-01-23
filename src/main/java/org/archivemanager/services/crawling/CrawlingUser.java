package org.archivemanager.services.crawling;


public interface CrawlingUser {
	public String getEmail();
	public String getPassword();
	public void setEmail(String email);
	public void setPassword(String password);
	
	public boolean isBusiness();
	public boolean isEntertainment();
	public boolean isLifestyle();
	public boolean isPolitics();
	public boolean isNews();
	public boolean isResearch();
	public boolean isSports();
	public boolean isTechnology();
	public boolean isWorld();
	
	public void setBusiness(boolean value);
	public void setEntertainment(boolean value);
	public void setLifestyle(boolean value);
	public void setPolitics(boolean value);
	public void setNews(boolean value);
	public void setResearch(boolean value);
	public void setSports(boolean value);
	public void setTechnology(boolean value);
	public void setWorld(boolean value);
	
}
