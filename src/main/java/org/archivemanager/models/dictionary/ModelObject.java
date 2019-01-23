package org.archivemanager.models.dictionary;

import java.io.Serializable;

import org.archivemanager.models.system.QName;

public interface ModelObject extends Serializable {

	Long getId();
	void setId(Long id);
	
	String getUid();
	void setUid(String uid);
	
	QName getQName();
	void setQName(QName qname);
	
	String getLabel();
	void setLabel(String name);
	
	String getDescription();
	void setDescription(String description);
	
}
