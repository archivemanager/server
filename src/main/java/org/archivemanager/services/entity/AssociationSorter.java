package org.archivemanager.services.entity;
import java.io.Serializable;
import java.util.Comparator;

import org.archivemanager.data.Sort;


public class AssociationSorter implements Comparator<Association>, Serializable {
	private static final long serialVersionUID = 3502967410907833006L;
	private EntityService entityService;
	private EntitySorter sorter;
	
	
	public AssociationSorter(EntityService entityService, Sort sort) {
		this.entityService = entityService;
		this.sorter = new EntitySorter(sort);
	}
	
	@Override
	public int compare(Association assoc1, Association assoc2) {
		if(assoc1 == null || assoc1 == null) return 0;
		try {
			if(assoc1.getQName().equals(assoc2.getQName())) {
				Entity targetEntity1 = entityService.getEntity(assoc1.getTarget());
				Entity targetEntity2 = entityService.getEntity(assoc2.getTarget());
				if(targetEntity1 != null && targetEntity2 != null) {
					return sorter.compare(targetEntity1, targetEntity2);
				} else {
					Entity sourceEntity1 = entityService.getEntity(assoc1.getSource());
					Entity sourceEntity2 = entityService.getEntity(assoc2.getSource());
					if(sourceEntity1 != null && sourceEntity2 != null)
						return sorter.compare(sourceEntity1, sourceEntity2);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
