package org.archivemanager.services.crawling;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Entity;


public class Crawler extends Entity {
	private static final long serialVersionUID = 8566486062162210388L;
	private byte[] iconData;
	private int newDocuments;
	private String lastMessage;
	
	private List<Search> searches = new ArrayList<Search>();
	private List<Seed> seeds = new ArrayList<Seed>();
	
	public static final int TYPE_GATHERER = 1;
	public static final int TYPE_WATCHER = 2;
	
	public static final int STATUS_RUNNING = 2;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_PAUSED = 0;
	public static final int STATUS_ERROR = -1;
	
	public static final int FREQ_MONTHLY = 1;
	public static final int FREQ_WEEKLY = 2;
	public static final int FREQ_DAILY = 3;
	public static final int FREQ_2DAILY = 4;
	public static final int FREQ_8HOUR = 5;
	public static final int FREQ_4HOUR = 6;
	public static final int FREQ_2HOUR = 7;
	public static final int FREQ_HOUR = 8;
	
	
	public Crawler() {
		setQName(CrawlingModel.CRAWLER);
	}
	public Crawler(Entity entity) {
		setId(entity.getId());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	public Crawler(String urlStr, String name) {
		setQName(CrawlingModel.CRAWLER);
		setUrl(urlStr);
		addProperty(SystemModel.NAME, name);		
		setFrequency(Crawler.FREQ_DAILY);
    	setStatus(Crawler.STATUS_ACTIVE);
    	setHops(1);
	}
	
	public void setEntity(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	public boolean isActive() {
		return getStatus() == Crawler.STATUS_ACTIVE;
	}
	public boolean isRunning() {
		return getStatus() == Crawler.STATUS_RUNNING;
	}
	public boolean isPaused() {
		return getStatus() == Crawler.STATUS_PAUSED;
	}
	
	public void setUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			setProtocol(url.getProtocol());
			setDomain(url.getHost());
			if(url.getPath() != null && url.getPath().length() > 0) setPath(url.getPath());
			else setPath("");
			if(url.getQuery() != null) setQuery(url.getQuery());
			else setQuery("");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getUrl() {
		String url = getProtocol() + "://" + getDomain();
		if(getPath() != null && getPath().length() > 0) url = url + getPath();
		if(getQuery() != null && getQuery().length() > 0) url = url + "?" + getQuery();
		return url;
	}
	
	public int getType() {
		String status = getPropertyValueString(CrawlingModel.TYPE);
		if(status != null && status.length() > 0) return Integer.valueOf(status);
		return 0;
	}
	public void setType(int type) {
		try {
			addProperty(CrawlingModel.TYPE, type);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.TYPE+" -> "+type);
		}
	}
	public String getUsername() {
		return getPropertyValueString(SystemModel.USERNAME);
	}
	protected void setUsername(String username) {
		try {
			addProperty(SystemModel.USERNAME, username);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.USERNAME+" -> "+username);
		}
	}
	public String getPassword() {
		return getPropertyValueString(SystemModel.PASSWORD);
	}
	protected void setPassword(String password) {
		try {
			addProperty(SystemModel.PASSWORD, password);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.PASSWORD+" -> "+password);
		}
	}
	public String getProtocol() {
		return getPropertyValueString(SystemModel.PROTOCOL);
	}
	protected void setProtocol(String protocol) {
		try {
			addProperty(SystemModel.PROTOCOL, protocol);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.PROTOCOL+" -> "+protocol);
		}
	}
	public String getDomain() {
		return getPropertyValueString(SystemModel.DOMAIN);
	}
	protected void setDomain(String domain) {
		//domain = domain.replace("google2.", "").replace("www.", "");
		try {
			addProperty(SystemModel.DOMAIN, domain);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.DOMAIN+" -> "+domain);
		}
	}
	public String getPath() {
		return getPropertyValueString(SystemModel.PATH);
	}
	protected void setPath(String path) {
		try {
			addProperty(SystemModel.PATH, path);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.PATH+" -> "+path);
		}
	}
	public String getQuery() {
		return getPropertyValueString(SystemModel.QUERY);
	}
	protected void setQuery(String query) {
		try {
			addProperty(SystemModel.QUERY, query);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.QUERY+" -> "+query);
		}
	}
	public String getCategory() {
		return getPropertyValueString(CrawlingModel.CATEGORY);
	}
	public void setCategory(String category) {
		try {
			addProperty(CrawlingModel.CATEGORY, category);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CATEGORY+" -> "+category);
		}
	}
	public long getIcon() {
		String image = getPropertyValueString(CrawlingModel.ICON);
		if(image != null && image.length() > 0) return Long.valueOf(image);
		return 0;
	}
	public void setIcon(long id) {
		try {
			addProperty(CrawlingModel.ICON, id);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.ICON+" -> "+id);
		}
	}
	public long getImage() {
		String image = getPropertyValueString(CrawlingModel.IMAGE);
		if(image != null && image.length() > 0) return Long.valueOf(image);
		return 0;
	}
	public void setImage(long id) {
		try {
			addProperty(CrawlingModel.IMAGE, id);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.IMAGE+" -> "+id);
		}
	}
	public String getMessage() {
		return getPropertyValueString(CrawlingModel.MESSAGE);
	}
	public void setMessage(String message) {
		try {
			addProperty(CrawlingModel.MESSAGE, message);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.MESSAGE+" -> "+message);
		}
	}
	public int getStatus() {
		String status = getPropertyValueString(CrawlingModel.STATUS);
		if(status != null && status.length() > 0) return Integer.valueOf(status);
		return Crawler.STATUS_PAUSED;
	}
	public void setStatus(int status) {
		try {
			addProperty(CrawlingModel.STATUS, status);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.STATUS+" -> "+status);
		}
	}
	public int getFrequency() {
		String frequency = getPropertyValueString(CrawlingModel.FREQUENCY);
		if(frequency != null && frequency.length() > 0) return Integer.valueOf(frequency);
		return Crawler.FREQ_DAILY;
	}
	public void setFrequency(int frequency) {
		try {
			addProperty(CrawlingModel.FREQUENCY, frequency);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.FREQUENCY+" -> "+frequency);
		}
	}
	public long getLastCrawl() {
		String lastCrawl = getPropertyValueString(CrawlingModel.LAST_CRAWL);
		if(lastCrawl != null && lastCrawl.length() > 0) return Long.valueOf(lastCrawl);
		return 0;
	}
	public void setLastCrawl(long lastCrawl) {
		try {
			addProperty(CrawlingModel.LAST_CRAWL, lastCrawl);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.LAST_CRAWL+" -> "+lastCrawl);
		}
	}
	public String getDayOfWeek() {
		return getPropertyValueString(CrawlingModel.DAY_OF_WEEK);
	}
	public void setDayOfWeek(String dayOfWeek) {
		try {
			addProperty(CrawlingModel.DAY_OF_WEEK, dayOfWeek);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.DAY_OF_WEEK+" -> "+dayOfWeek);
		}
	}
	public String getTimeOfDay1() {
		return getPropertyValueString(CrawlingModel.TIME_OF_DAY_1);
	}
	public void setTimeOfDay1(String timeOfDay) {
		try {
			addProperty(CrawlingModel.TIME_OF_DAY_1, timeOfDay);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.TIME_OF_DAY_1+" -> "+timeOfDay);
		}
	}
	public String getTimeOfDay2() {
		return getPropertyValueString(CrawlingModel.TIME_OF_DAY_2);
	}
	public void setTimeOfDay2(String timeOfDay) {
		try {
			addProperty(CrawlingModel.TIME_OF_DAY_2, timeOfDay);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.TIME_OF_DAY_2+" -> "+timeOfDay);
		}
	}
	public int getHops() {
		String hops = getPropertyValueString(CrawlingModel.HOPS);
		if(hops != null && hops.length() > 0) return Integer.valueOf(hops);
		return 1;
	}
	public void setHops(int hops) {
		try {
			addProperty(CrawlingModel.HOPS, hops);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.HOPS+" -> "+hops);
		}
	}
	public int getMaxResults() {
		String count = getPropertyValueString(CrawlingModel.MAX_RESULTS);
		if(count != null && count.length() > 0) return Integer.valueOf(count);
		return 0;
	}
	public void setMaxResults(int count) {
		try {
			addProperty(CrawlingModel.MAX_RESULTS, count);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.MAX_RESULTS+" -> "+count);
		}
	}
	public boolean isSameDomain() {
		String domain = getPropertyValueString(CrawlingModel.SAME_DOMAIN);
		if(domain != null && domain.length() > 0 && domain.equals("false")) return false;
		return true;
	}
	public void setSameDomain(boolean domain) {
		try {
			addProperty(CrawlingModel.SAME_DOMAIN, domain);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.SAME_DOMAIN+" -> "+domain);
		}
	}
	public boolean isLoaded() {
		String domain = getPropertyValueString(CrawlingModel.LOADED);
		if(domain != null && domain.length() > 0 && domain.equals("false")) return false;
		return true;
	}
	public void setLoaded(boolean loaded) {
		try {
			addProperty(CrawlingModel.LOADED, loaded);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.LOADED+" -> "+loaded);
		}
	}
	public boolean isCached() {
		String domain = getPropertyValueString(CrawlingModel.CACHED);
		if(domain != null && domain.length() > 0 && domain.equals("false")) return false;
		return true;
	}
	public void setCached(boolean cached) {
		try {
			addProperty(CrawlingModel.CACHED, cached);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CACHED+" -> "+cached);
		}
	}
	public boolean isAnonymous() {
		String domain = getPropertyValueString(CrawlingModel.ANONYMOUS);
		if(domain != null && domain.length() > 0 && domain.equals("true")) return true;
		return false;
	}
	public void setAnonymous(boolean anonymous) {
		try {
			addProperty(CrawlingModel.ANONYMOUS, anonymous);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.ANONYMOUS+" -> "+anonymous);
		}
	}
	public long getNextCrawl() {
		String nextCrawl = getPropertyValueString(CrawlingModel.NEXT_CRAWL);
		if(nextCrawl != null && nextCrawl.length() > 0) return Long.valueOf(nextCrawl);
		return 0;
	}
	public void setNextCrawl(long nextCrawl) {
		try {
			addProperty(CrawlingModel.NEXT_CRAWL, nextCrawl);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.NEXT_CRAWL+" -> "+nextCrawl);
		}
	}
	public String getConfiguration() {
		return getPropertyValueString(CrawlingModel.CONFIGURATION);
	}
	public void setConfiguration(String configuration) {
		try {
			addProperty(CrawlingModel.CONFIGURATION, configuration);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CONFIGURATION+" -> "+configuration);
		}
	}
	public String getConfigurationId() {
		return getPropertyValueString(CrawlingModel.CONFIGURATION_ID);
	}
	public void setConfigurationId(String configuration) {
		try {
			addProperty(CrawlingModel.CONFIGURATION_ID, configuration);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CONFIGURATION_ID+" -> "+configuration);
		}
	}
	
	public byte[] getIconData() {
		return iconData;
	}
	public void setIconData(byte[] iconData) {
		this.iconData = iconData;
	}
	public int getNewDocuments() {
		return newDocuments;
	}
	public void setNewDocuments(int newDocuments) {
		this.newDocuments = newDocuments;
	}	
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public List<Search> getSearches() {
		return searches;
	}
	public void setSearches(List<Search> searches) {
		this.searches = searches;
	}
	public List<Seed> getSeeds() {
		return seeds;
	}
	public void setSeeds(List<Seed> seeds) {
		this.seeds = seeds;
	}
}
