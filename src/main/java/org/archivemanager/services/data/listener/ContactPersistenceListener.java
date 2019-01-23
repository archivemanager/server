package org.archivemanager.services.data.listener;

import org.archivemanager.models.ContactModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;


public class ContactPersistenceListener extends BasePersistenceListener {

	
	@Override
	public void onBeforeAdd(Entity entity) {
		try {
			if(entity.getQName().equals(ContactModel.INDIVIDUAL)) {
				String name = "";
				if(entity.getPropertyValue(ContactModel.LAST_NAME) != null) name += entity.getPropertyValue(ContactModel.LAST_NAME);
				if(entity.getPropertyValue(ContactModel.FIRST_NAME) != null) {
					if(name.length() > 0) name += ", ";
					name += entity.getPropertyValue(ContactModel.FIRST_NAME);
				}
				if(entity.getPropertyValue(ContactModel.MIDDLE_NAME) != null) name += " "+entity.getPropertyValue(ContactModel.MIDDLE_NAME);
				entity.addProperty(SystemModel.NAME, name);
			} else {
			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAfterAdd(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBeforeUpdate(Entity oldValue, Entity newValue) {
		try {
			if(newValue.getQName().equals(ContactModel.INDIVIDUAL)) {
				String name = "";
				if(newValue.getPropertyValue(ContactModel.LAST_NAME) != null) name += newValue.getPropertyValue(ContactModel.LAST_NAME);
				if(newValue.getPropertyValue(ContactModel.FIRST_NAME) != null) {
					if(name.length() > 0) name += ", ";
					name += newValue.getPropertyValue(ContactModel.FIRST_NAME);
				}
				if(newValue.getPropertyValue(ContactModel.MIDDLE_NAME) != null) name += " "+newValue.getPropertyValue(ContactModel.MIDDLE_NAME);
				newValue.addProperty(SystemModel.NAME, name);
			} else {
			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
