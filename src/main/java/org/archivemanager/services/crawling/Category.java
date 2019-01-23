package org.archivemanager.services.crawling;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;


public class Category extends Entity {
	private static final long serialVersionUID = -270086908998496601L;	
	    
    
	public Category() {
		setQName(CrawlingModel.CATEGORY);
	}
	public Category(String name) {
		addProperty(SystemModel.NAME, name);
		setQName(CrawlingModel.CATEGORY);
	}
	public Category(Long id, String name) {
		setId(id);
		addProperty(SystemModel.NAME, name);
		setQName(CrawlingModel.CATEGORY);
	}
	public Category(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	
	public String getDescription() {
		return getPropertyValueString(SystemModel.DESCRIPTION);
	}
	public void setDescription(String description) {
		try {
			addProperty(SystemModel.DESCRIPTION, description);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.DESCRIPTION+" -> "+description);
		}
	}
	public List<Crawler> getCrawlers() {
		List<Crawler> crawlers = new ArrayList<Crawler>();
		List<Association> assocs = getAssociations(CrawlingModel.CRAWLERS);
		for(Association assoc : assocs) {
			//crawlers.add((Crawler)assoc.getTargetEntity());
		}
		return crawlers;
	}
	public void setCrawlers(List<Crawler> crawlers) {
		for(Crawler crawler : crawlers) {
			if(crawler instanceof Entity) {
				Association assoc = new Association(CrawlingModel.CRAWLERS, this, (Entity)crawler);
				getAssociations().add(assoc);
			}
		}
	}
	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();
		List<Association> assocs = getAssociations(CrawlingModel.CATEGORIES);
		for(Association assoc : assocs) {
			//categories.add((Category)assoc.getTargetEntity());
		}
		return categories;
	}
	public void setCategories(List<Category> categories) {
		for(Category category : categories) {
			Association assoc = new Association(CrawlingModel.CATEGORIES, this, category);
			getAssociations().add(assoc);
		}
	}
	
}
