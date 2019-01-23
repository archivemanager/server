package org.archivemanager.services.search.indexing;
import java.util.logging.Logger;

import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.scheduling.JobSupport;
import org.jfree.util.Log;


public class EntityIndexingJob extends JobSupport {
	private static final long serialVersionUID = -5348083461532426560L;
	private final static Logger log = Logger.getLogger(EntityIndexingJob.class.getName());
	private EntityService entityService;
	private QName qname;
	private int page;
	private int size;
	
	
	public EntityIndexingJob(EntityService entityService, QName qname, int page, int size) {
		this.entityService = entityService;
		this.qname = qname;
		this.page = page;
		this.size = size;
	}
	
	@Override
	public void execute() {
		super.execute();
		int end = (page*size) + size;
		EntityResultSet results = entityService.getEntities(qname, page, size);
		try {						
			entityService.index(results.getData());			
			setComplete(true);
			Log.info("search index added "+end+" "+qname.getLocalName()+"s");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setLastMessage(String msg) {
		log.info(msg);
		super.setLastMessage(msg);
	}
}
