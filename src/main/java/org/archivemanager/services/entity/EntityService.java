package org.archivemanager.services.entity;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.archivemanager.models.system.QName;


public interface EntityService extends Serializable {
	
	int count(QName qname);	
	
	List<Entity> getEntities(QName[] qnames);
	EntityResultSet getEntities(QName qname, int page, int size);
	//EntityResultSet getEntities(QName qname, QName propertyQname, int page, int size);
	//EntityResultSet getEntities(QName qname, QName propertyQname, Object value, int page, int size);
	
	Entity getEntity(long id);
	Entity getEntity(QName qname, QName propertyQname, Object value);
		
	void updateEntity(Entity entity);
	void addEntities(Collection<Entity> entities);
	void removeEntity(long id);
	
	List<Association> getSourceAssociations(long entityId);
	List<Association> getTargetAssociations(long entityId);
	
	Association getAssociation(long id);	
	Association getAssociation(QName qname, long source, long target);
	
	void updateAssociation(Association association);
	void removeAssociation(long id);		
	
	void index(Long id);
	void index(Entity id);
	void index(List<Entity> entities);
	
}
