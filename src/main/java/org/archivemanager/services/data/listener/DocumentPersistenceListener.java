package org.archivemanager.services.data.listener;

public class DocumentPersistenceListener extends DefaultEntityPersistenceListener {
	//private final static Log log = LogFactory.getLog(DocumentPersistenceListener.class);
	
	/*
	@Override
	public List<IndexField> index(Entity entity) {
		List<IndexField> fields = super.index(entity);
		try {
			
			Map<String,Object> properties = nodeService.getNodeProperties(entity.getId());
    		//properties.put("xid", entity.getXid());
    		for(String key : properties.keySet()) {
    			if(key.equals(SystemModel.NAME.toString())) {
    				String value = (String)properties.get(key);
    				if(value != null) {
    					value = HTMLUtility.removeTags(value.replace(",", ""));
    					properties.put(key, value);    
    				}
    			}
    		}
    		
    		List<Association> seeds = entity.getTargetAssociations(CrawlingModel.DOCUMENTS);
    		StringBuffer buff = new StringBuffer();
    		for(Association seedAssoc : seeds) {
    			Entity seed = getEntityService().getEntity(seedAssoc.getSource());
    			List<Association> crawlers = seed.getTargetAssociations(CrawlingModel.SEEDS);
    			for(Association crawlerAssoc : crawlers) {
    				buff.append(crawlerAssoc.getSource()+" ");
    			}
    		}
    		if(buff.length() > 0) {
    			fields.add(new IndexField(ModelField.TYPE_SMALLTEXT, CrawlingModel.CRAWLERS.toString(), buff.toString(),true));
    		}    		
		} catch(Exception e) {
			log.error("", e);
		}
		return fields;
	}
	*/
}
