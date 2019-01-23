package org.archivemanager.services.entity.neo4j;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.cache.CacheService;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityPersistenceListener;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.ImportProcessor;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.search.indexing.IndexingService;
import org.neo4j.driver.internal.value.NullValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RemoteNeo4jEntityService implements EntityService {
	private static final long serialVersionUID = 7870373534164732513L;
	private final static Logger log = Logger.getLogger(RemoteNeo4jEntityService.class.getName());
	private CacheService cacheService;
	private DataDictionaryService dictionaryService;
	private RemoteNeo4jService neo4jService;
	private PropertyConfiguration properties;
	@Autowired private IndexingService indexingService;
	
	private Map<String, List<ImportProcessor>> importers = new HashMap<String, List<ImportProcessor>>();
	@Autowired private List<EntityPersistenceListener> persistenceListeners = new ArrayList<EntityPersistenceListener>();
		
	
	public RemoteNeo4jEntityService(PropertyConfiguration properties, CacheService cacheService, DataDictionaryService dictionaryService, RemoteNeo4jService neo4jService) {
		this.properties = properties;
		this.cacheService = cacheService;
		this.dictionaryService = dictionaryService;
		this.neo4jService = neo4jService;
	}
	
	public int count(QName qname) {
		Session session = neo4jService.session();
		String query = "MATCH (n1) WHERE n1.qname='"+qname.toString()+"' return count(*)";
		try {
			StatementResult countResult = session.run(query);
			while(countResult.hasNext()) {
				Record record = countResult.next();
				return record.get(0).asInt();
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return 0;
	}
	
	public List<Entity> getEntities(QName[] qnames) {
		List<Entity> entities = new ArrayList<Entity>();
		Session session = neo4jService.session();
		String query = "MATCH (n1) WHERE n1.qname IN [";
		for(int i=0; i < qnames.length; i++) {
			query += "'"+qnames[i].toString()+"'";
			if(i < qnames.length-1) query += ",";
		}
		query += "] return n1";		
		try {			
			StatementResult mainResult = session.run(query);			
			org.neo4j.driver.v1.types.Node n = null;
			while(mainResult.hasNext()) {
				Record record = mainResult.next();
				n = record.get("n1").asNode();
				Entity entity = getEntity(n.id());
				entities.add(entity);					
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return entities;
	}
	public EntityResultSet getEntities(QName qname, int page, int size) {
		long startTime = System.currentTimeMillis();
		EntityResultSet entities = new EntityResultSet();
		int start = (page*size) -  size;
		Session session = neo4jService.session();
		String query = "MATCH (n1) WHERE n1.qname='"+qname.toString()+"'";		
		try {
			StatementResult countResult = session.run(query+" return count(*)");
			while(countResult.hasNext()) {
				Record record = countResult.next();
				entities.setSize(record.get(0).asInt());
			}
			String resultQuery = query+" return n1";
			if(start > 0) resultQuery += " skip "+start;
			if(size > 0) resultQuery += " limit "+size;
			StatementResult mainResult = session.run(resultQuery);			
			org.neo4j.driver.v1.types.Node n = null;
			while(mainResult.hasNext()) {
				Record record = mainResult.next();
				n = record.get("n1").asNode();
				Entity entity = getEntity(n.id());
				entities.getData().add(entity);					
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		log.info("entity search {count="+entities.getSize()+",qname="+qname.toString()+",page="+page+",size="+size+"} completed in "+(System.currentTimeMillis() - startTime)+" ms");
		return entities;
	}
	
	//@Override
	public EntityResultSet getEntities(QName qname, QName propertyQname, int page, int size) {
		EntityResultSet entities = new EntityResultSet();
		int start = (page*size) -  size;
		int end = page*size;
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n) WHERE n."+qname.toString()+" IS NOT NULL return n, count(*);");
		try {
			int count = 0;
			org.neo4j.driver.v1.types.Node n = null;
			while(result.hasNext()) {
				if(count >= start) {
					Record record = result.next();
					n = record.get("n1").asNode();
					Entity entity = getEntity(n.id());
					entities.getData().add(entity);					
				}
				if(count >= end) break;
				count++;
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return entities;
	}
	
	//@Override
	public EntityResultSet getEntities(QName qname, QName propertyQname, Object value, int page, int size) {
		EntityResultSet entities = new EntityResultSet();
		int start = (page*size) -  size;
		int end = page*size;
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n) WHERE n."+qname.toString()+"='"+value+"' return n, count(*);");
		try {
			int count = 0;
			org.neo4j.driver.v1.types.Node n = null;
			while(result.hasNext()) {
				if(count >= start) {
					Record record = result.next();
					n = record.get("n1").asNode();
					Entity entity = getEntity(n.id());
					entities.getData().add(entity);					
				}
				if(count >= end) break;
				count++;
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return entities;
	}
	public Entity getEntity(long id) {
		Entity entity = (Entity) cacheService.get("nodes", String.valueOf(id));
		if(entity == null) {
			Session session = neo4jService.session();
			StatementResult result = session.run("MATCH (n1)-[r]-(n2) WHERE ID(n1)="+id+" return n1,r,n2;");
			try {
				org.neo4j.driver.v1.types.Node n1 = null;
				org.neo4j.driver.v1.types.Node n2 = null;
				while(result.hasNext()) {
					Record record = result.next();
					if(entity == null) {
						n1 = record.get("n1").asNode();
						Value value = record.get("n2");
						if(!value.isNull()) 
							n2 = value.asNode();
						if(n1.id() == id) {
							entity = getEntity(n1);
						}
					}
					if(entity != null) {
						Relationship r = record.get("r").asRelationship();
						QName qname = QName.createQualifiedName(r.type());
						QName startName = QName.createQualifiedName(n1.get("qname").asString());
						QName endName = QName.createQualifiedName(n2.get("qname").asString());
						Association association = new Association(qname, r.startNodeId(), r.endNodeId());
						association.setId(r.id());
						if(startName != null) association.setSourceName(startName);
						if(endName != null) association.setTargetName(endName);						
						for(String key : r.keys()) {
							QName q = QName.createQualifiedName(key);
							//ModelField field = model.getAssociationField(qname, q);
							//if(field != null) {
								Value val = r.get(key);
								association.addProperty(q, val.asObject());
							//}
						}
						if(r.startNodeId() == n1.id())
							entity.getSourceAssociations().add(association);
						else
							entity.getTargetAssociations().add(association);
					}
				}
				if(entity == null) {
					StatementResult result2 = session.run("MATCH (n1) WHERE ID(n1)="+id+" return n1;");
					while(result2.hasNext()) {
						Record record = result2.next();
						n1 = record.get("n1").asNode();
						if(n1.id() == id)
							entity = getEntity(n1);
					}
				}
			} catch(Exception e) {
				log.log(Level.SEVERE, "", e);
			} finally {
				session.close();
			}			
		}		
		return entity;
	}

	//@Override
	public Entity getEntity(QName qname, QName propertyQname, Object value) {
		Session session = neo4jService.session();
		String query = "MATCH (n) WHERE n."+propertyQname.toString()+"='"+value+"' return n;";
		StatementResult result = session.run(query);
		org.neo4j.driver.v1.types.Node n = null;
		while(result.hasNext()) {
			Record record = result.next();
			if(n == null) {
				n = record.get("n").asNode();
				return getEntity(n.id());
			}
		}
		return null;
	}
	/*
	protected void getEntityRelationship(Entity entity, Record record) throws Exception {
		Node n = record.get("n1").asNode();
		org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
		QName qname = QName.createQualifiedName(r.type());
		Model model = dictionaryService.getModel(entity.getQName());
		if(r.startNodeId() == n.id()) {
			Association association = new Association(qname, r.startNodeId(), r.endNodeId());
			association.setId(r.id());
			for(String key : r.keys()) {
				QName q = QName.createQualifiedName(key);
				ModelField field = model.getAssociationField(qname, q);
				if(field != null) {
					Value val = r.get(key);
					association.addProperty(q, val.asObject());
				}
			}
			org.neo4j.driver.v1.types.Node targetNode = record.get("n2").asNode();
			Entity targetEntity = getEntity(targetNode);
			if(targetEntity != null) {
				entity.getSourceAssociations().add(association);
			}
		} else {			
			Association association = new Association(qname, r.startNodeId(), r.endNodeId());
			association.setId(r.id());
			for(String key : r.keys()) {
				QName q = QName.createQualifiedName(key);
				ModelField field = model.getAssociationField(qname, q);
				if(field != null) {
					Value val = r.get(key);
					association.addProperty(q, val.asObject());
				}
			}
			org.neo4j.driver.v1.types.Node sourceNode = record.get("n2").asNode();
			Entity sourceEntity = getEntity(sourceNode);
			if(sourceEntity != null) {
				entity.getTargetAssociations().add(association);
			}
		}
	}
	*/	
	protected Entity getEntity(org.neo4j.driver.v1.types.Node n) throws Exception {		
		Value qnameValue = n.get("qname");
		if(qnameValue instanceof NullValue) {
			log.info("missing qname");
		} else {
			QName qname = QName.createQualifiedName(qnameValue.asString());
			Model model = dictionaryService.getModel(qname);
			Entity node = new Entity(n.id(), qname);		
			if(model != null) {
				Map<String, Object> map = n.asMap();
				for(String key : map.keySet()) {
					Value val = n.get(key);
					if(key.equals("uid") || key.equals("uuid")) node.setUid(val.asString());					
					else if(key.equals("description")) node.addProperty(SystemModel.DESCRIPTION, val.asString());
					else if(key.equals("qname")) node.setQName(QName.createQualifiedName(val.asString()));
					else if(key.equals("accessed")) node.setAccessed(val.asLong());
					else if(key.equals("created")) node.setCreated(val.asLong());
					else if(key.equals("creator")) node.setCreator(val.asLong());
					else if(key.equals("modified")) node.setModified(val.asLong());
					else if(key.equals("modifier")) node.setModifier(val.asLong());
					else if(key.equals("xid")) node.setXid(val.asLong());
					else if(key.equals("user")) node.setUser(val.asLong());
					else if(key.equals("index")) node.addProperty(new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE, "index"), val.asObject());
					else if(key.equals("deleted")) node.setDeleted(val.asBoolean());					
				}
				List<ModelField> fields = dictionaryService.getModelFields(qname);
				for(ModelField field : fields){
					try {
						Object value = map.get(field.getQName().toString());
						node.addProperty(field.getType(), field.getQName(), value);						
					} catch(Exception e) {
						log.log(Level.SEVERE, "", e);
					}					
				}
				for(EntityPersistenceListener listener : persistenceListeners) {
					listener.onSelect(node);
				}
				return node;				
			} else if(properties.getNodeCleanup().contains(qname.toString())) {
				neo4jService.removeNode(n.id());
				log.log(Level.INFO,"cleaned up node:"+n.id()+", qname="+qname.toString());
			} else if(properties.getRelationshipCleanup().contains(qname.toString())) {
				
			}
		}
		return null;
	}
	public void removeEntity(long id) {
		Session session = neo4jService.session();
		try	{
			/** Cascade the delete to entities marked so in the data dictionary
			 *  regardless, delete them from the cache so they are reloaded on next request. **/
			Entity entity = getEntity(id);
			if(entity != null) {
				Model model = dictionaryService.getModel(entity.getQName());
				if(model != null) {
					for(EntityPersistenceListener listener : persistenceListeners) {
						listener.onBeforeDelete(entity);
					}
					
					List<QName> cascades = new ArrayList<QName>();
					while(model != null) {
						for(ModelRelation relation : model.getRelations()) {
							if(relation.isCascade()) {
								cascades.add(relation.getQName());
							}
						}
						if(model.getParentName() != null) model = dictionaryService.getModel(model.getParentName());
						else break;
					}
					if(cascades.size() > 0) {
						List<Long> processedIds = new ArrayList<Long>();
						for(Association assoc : entity.getSourceAssociations()) {
							if(cascades.contains(assoc.getQName())) {
								if(assoc.getTarget() != id && !processedIds.contains(assoc.getTarget())) {
									removeEntity(assoc.getTarget());
									processedIds.add(assoc.getTarget());
								}
							}
						}
					}
					for(Association assoc : entity.getTargetAssociations()) {
						removeAssociation(assoc.getId());
					}
					StatementResult result = session.run("MATCH (n) WHERE ID(n) = "+entity.getId()+" DETACH DELETE n;");
					while(result.hasNext()) {
						Record record = result.next();
						org.neo4j.driver.v1.types.Node n = record.get("r").asNode();
						log.log(Level.INFO, "deleted entity:"+n.id());
					}
					log.log(Level.INFO,"removed node:"+entity.getId());
					
					for(EntityPersistenceListener listener : persistenceListeners) {
						listener.onAfterDelete(entity);
					}
				}				
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
	}
	
	public void addEntities(Collection<Entity> entities) {
		Map<String,Long> idMap = new HashMap<String,Long>();
		try	{
			int i = 0;
			for(Entity entity : entities) {
				addEntity(entity);
				idMap.put(entity.getUid(), entity.getId());
				i++;
				if(i % 10 == 0) System.out.println(i+" of "+entities.size()+" nodes migrated");
			}
			i = 0;
			for(Entity entity : entities) {
				List<Association> sourceAssociations = entity.getSourceAssociations();
				for(Association assoc : sourceAssociations) {
					try	{
						//String qname = assoc.getQName().toString();
						Long sourceid = entity.getId();
						Long targetid = assoc.getTarget();
						if(targetid == null) targetid = idMap.get(assoc.getTargetUid());
						if(sourceid != null && targetid != null) {
							assoc.setSource(sourceid);
							assoc.setTarget(targetid);
							addAssociation(assoc);
						} else {
							System.out.println("bad news on id lookup:"+sourceid+" to "+targetid);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				List<Association> targetAssociations = entity.getTargetAssociations();
				for(Association assoc : targetAssociations) {
					try	{
						//String qname = assoc.getQName().toString();
						Long sourceid = assoc.getSource();
						Long targetid = entity.getId();
						if(sourceid == null) sourceid = idMap.get(assoc.getSourceUid());
						if(sourceid != null && targetid != null) {
							assoc.setSource(sourceid);
							assoc.setTarget(targetid);
							addAssociation(assoc);
						} else {
							System.out.println("bad news on id lookup:"+sourceid+" to "+targetid);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				i++;
				if(i % 10 == 0) System.out.println(i+" of "+entities.size()+" nodes relationships migrated");
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	public void updateEntity(Entity entity) {
		if(entity.getId() == null || entity.getId().equals(0L)) {
			addEntity(entity);
			return;
		}
		Session session = neo4jService.session();
		try	{
			Entity oldValue = getEntity(entity.getId());
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onBeforeUpdate(oldValue, entity);
			}
			if(entity.getId() != null) {				
				Map<String,Object> map = toMap(entity);
				session.run("MATCH (n) WHERE ID(n) = "+entity.getId()+" SET n=$map return n;", map);
				/*
				for(Association assoc : entity.getSourceAssociations()) {
					if(assoc.getId() == null) {
						//assoc.setSourceEntity(entity);
						//addAssociation(assoc);
					}
				}
				for(Association assoc : entity.getTargetAssociations()) {
					if(assoc.getId() == null) {
						//assoc.setTargetEntity(entity);
						//addAssociation(assoc);
					}
				}
				*/
			}
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onAfterUpdate(oldValue, entity);
			}
			log.log(Level.INFO,"updated node:"+entity.getId());
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
	}
	protected void addEntity(Entity entity) {
		try	{
			if(entity.getQName() == null) throw new InvalidEntityException("entity model not found for missing qname");
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onBeforeAdd(entity);
			}
			Model model = dictionaryService.getModel(entity.getQName());
			if(model.isStored()) {
				Session session = neo4jService.session();
				Map<String,Object> map = toMap(entity);
				StatementResult result = session.run("CREATE (n $map) return n;", map);
				org.neo4j.driver.v1.types.Node n = null;
				while(result.hasNext()) {
					Record record = result.next();
					n = record.get("n").asNode();
					entity.setId(n.id());
					break;
				}
				
				List<Association> sourceAssociations = entity.getSourceAssociations();
				for(Association assoc : sourceAssociations) {
					if(assoc.getId() == null) {
						//assoc.setSourceEntity(entity);
						addAssociation(assoc);
					}
				}
				List<Association> targetAssociations = entity.getTargetAssociations();
				for(Association assoc : targetAssociations) {
					if(assoc.getId() == null) {
						//assoc.setTargetEntity(entity);
						addAssociation(assoc);
					}
				}
			}
			//log.log(Level.INFO,"added node:"+entity.getId());
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onAfterAdd(entity);
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "entity model not found for qname:"+entity.getQName().toString(), e);
		}
	}
	public void cascadeProperty(QName qname, Long id, QName association, QName propertyName, Serializable propertyValue) {
		try	{
			Entity entity = getEntity(id);
			List<Association> associations = entity.getSourceAssociations(association);
			for(Association assoc : associations) {
				Entity targetEntity = getEntity(assoc.getTarget());
				targetEntity.addProperty(propertyName, propertyValue);
				updateEntity(targetEntity);
				cascadeProperty(targetEntity.getQName(), targetEntity.getId(), association, propertyName, propertyValue);
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

	public void removeAssociation(long id) {
		Session session = neo4jService.session();
		try	{
			Association association = getAssociation(id);
			Entity source = getEntity(association.getSource());			
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onBeforeAssociationDelete(association);
			}
			session.run("MATCH (n1)-[r]-() WHERE ID(r)="+association.getId() + " and ID(n1)=" + source.getId() + " DELETE r");
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onAfterAssociationDelete(association);
			}
			log.log(Level.INFO,"removed assoc:"+id);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
	}
	
	public Association getAssociation(long id) {
		Association association = null;
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n1)-[r]->(n2) WHERE id(r) = "+id+" return r;");
		try {
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				QName qname = QName.createQualifiedName(r.type());
				Entity startEntity = getEntity(r.startNodeId());
				Entity endEntity = getEntity(r.endNodeId());
				ModelRelation model = dictionaryService.getModelRelation(qname, startEntity.getQName(), endEntity.getQName());
				association = new Association(qname, r.startNodeId(), r.endNodeId());
				association.setId(r.id());
				for(String key : r.keys()) {
					QName q = QName.createQualifiedName(key);
					ModelField field = model.getField(q);
					if(field != null) {
						Value val = r.get(key);
						association.addProperty(q, val.asObject());
					}

				}				
				break;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return association;
	}
	public List<Association> getAssociations(long entityId) {
		List<Association> list = new ArrayList<Association>();
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n1)-[r]->(n2) WHERE ID(n1)="+entityId+" return n1,r,n2;");
		try {
			while(result.hasNext()) {
				Record record = result.next();
				Node n1 = record.get("n1").asNode();
				Node n2 = record.get("n2").asNode();
				Relationship r = record.get("r").asRelationship();
				QName qname = QName.createQualifiedName(r.type());
				QName startQname = QName.createQualifiedName(n1.get("qname").asString());
				QName endQname = QName.createQualifiedName(n2.get("qname").asString());
				ModelRelation model = dictionaryService.getModelRelation(startQname, qname);
				
				Association a = new Association(qname, r.startNodeId(), r.endNodeId());
				for(String key : r.keys()) {
					QName q = QName.createQualifiedName(key);
					ModelField field = model.getField(q);
					if(field != null) {
						Value val = r.get(key);
						a.addProperty(q, val.asObject());
					}

				}
				a.setSourceName(startQname);
				a.setTargetName(endQname);
				list.add(a);
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return list;
	}
	@Override
	public List<Association> getSourceAssociations(long entityId) {
		List<Association> list = new ArrayList<Association>();
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n1)-[r]->(n2) WHERE ID(n1)="+entityId+" return n1,r,n2;");
		try {
			while(result.hasNext()) {
				Record record = result.next();
				Node n1 = record.get("n1").asNode();
				Node n2 = record.get("n2").asNode();
				Relationship r = record.get("r").asRelationship();
				QName qname = QName.createQualifiedName(r.type());
				QName startQname = QName.createQualifiedName(n1.get("qname").asString());
				QName endQname = QName.createQualifiedName(n2.get("qname").asString());
				ModelRelation model = dictionaryService.getModelRelation(startQname, qname);
				
				Association a = new Association(qname, r.startNodeId(), r.endNodeId());
				for(String key : r.keys()) {
					QName q = QName.createQualifiedName(key);
					ModelField field = model.getField(q);
					if(field != null) {
						Value val = r.get(key);
						a.addProperty(q, val.asObject());
					}

				}
				a.setSourceName(startQname);
				a.setTargetName(endQname);
				list.add(a);
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return Collections.emptyList();
	}
	@Override
	public List<Association> getTargetAssociations(long entityId) {
		List<Association> list = new ArrayList<Association>();
		Session session = neo4jService.session();
		StatementResult result = session.run("MATCH (n1)-[r]->(n2) WHERE ID(n1)="+entityId+" return n1,r,n2;");
		try {
			while(result.hasNext()) {
				Record record = result.next();
				Node n1 = record.get("n1").asNode();
				Node n2 = record.get("n2").asNode();
				Relationship r = record.get("r").asRelationship();
				QName qname = QName.createQualifiedName(r.type());
				QName startQname = QName.createQualifiedName(n1.get("qname").asString());
				QName endQname = QName.createQualifiedName(n2.get("qname").asString());
				ModelRelation model = dictionaryService.getModelRelation(startQname, qname);
				
				Association a = new Association(qname, r.startNodeId(), r.endNodeId());
				for(String key : r.keys()) {
					QName q = QName.createQualifiedName(key);
					ModelField field = model.getField(q);
					if(field != null) {
						Value val = r.get(key);
						a.addProperty(q, val.asObject());
					}

				}
				a.setSourceName(startQname);
				a.setTargetName(endQname);
				list.add(a);
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return Collections.emptyList();
	}
	public Association getAssociation(QName qname, long source, long target) {
		Association association = null;
		Session session = neo4jService.session();
		String query = "MATCH (n1)-[r]->(n2) WHERE id(n1) = "+source+" and id(n2) = "+target+" and r.qname = '"+qname.toString()+"' return n1,r,n2;";
		StatementResult result = session.run(query);
		try {
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				Entity startEntity = getEntity(r.startNodeId());
				Entity endEntity = getEntity(r.endNodeId());
				ModelRelation model = dictionaryService.getModelRelation(qname, startEntity.getQName(), endEntity.getQName());
				association = new Association(QName.createQualifiedName(r.type()), startEntity, endEntity);
				association.setId(r.id());
				for(String key : r.keys()) {
					QName q = QName.createQualifiedName(key);
					ModelField field = model.getField(q);
					if(field != null) {
						Value val = r.get(key);
						association.addProperty(q, val.asObject());
					}

				}
				break;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return association;
	}

	protected void addAssociation(Association association) {
		if(association.getQName() == null || association.getSource() == null || association.getTarget() == null) return;
		Association assoc = getAssociation(association.getQName(), association.getSource(), association.getTarget());
		if(assoc != null) {
			log.log(Level.INFO, "duplicate association rejected qname:"+association.getQName()+", source:"+association.getSource()+", "
					+ "target:"+association.getTarget());
			return;
		}
		Session session = neo4jService.session();
		String query = "MATCH (n1),(n2) WHERE id(n1) = "+association.getSource()+" and id(n2) = "+association.getTarget();
		query += " CREATE (n1)-[r:"+association.getQName().toString()+"]->(n2) return r;";
		
		StatementResult result = session.run(query, toMap(association));
		try	{
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onBeforeAssociationAdd(association);
			}
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				association.setId(r.id());
				
				Map<String,Object> map = toMap(association);
				if(map.size() > 0) {
					StatementResult result2 = session.run("MATCH ()-[r]-() WHERE ID(r) = "+r.id()+" SET r=$map return r;", map);
					org.neo4j.driver.v1.types.Relationship rel = null;
					while(result2.hasNext()) {
						Record record2 = result2.next();
						rel = record2.get("r").asRelationship();
						log.log(Level.INFO, "updated association id:"+rel.id()+", qname:"+rel.type());
						for(String p : map.keySet()) {
							log.log(Level.INFO, "--updated association property id:"+rel.id()+", qname:"+p+", value="+map.get(p));
						}
					}
				} else log.log(Level.INFO, "updated association id:"+r.id()+", qname:"+r.type()+", start:"+r.startNodeId()+", end:"+r.endNodeId());
			}
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onAfterAssociationAdd(association);
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
	}
	public void updateAssociation(Association association) {
		if(association.getId() == null || association.getId().equals(0L)) {
			addAssociation(association);
			return;
		}
		try	{
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onBeforeAssociationUpdate(association);
			}			
			removeAssociation(association.getId());
			addAssociation(association);
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onAfterAssociationUpdate(association);
			}
			log.log(Level.INFO,"updated assoc:"+association.getId());
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}		
	
	protected Map<String,Object> toMap(Entity entity) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(entity.getUid() == null || entity.getUid().length() == 0) entity.setUid(java.util.UUID.randomUUID().toString());
		Map<String,Object> properties = new HashMap<String,Object>();
		map.put("map", properties);
		properties.put("uid", entity.getUid());
		properties.put("accessed", entity.getAccessed());
		properties.put("created", entity.getCreated());
		properties.put("creator", entity.getCreator());
		properties.put("modified", entity.getModified());
		properties.put("modifier", entity.getModifier());
		properties.put("xid", entity.getXid());
		properties.put("user", entity.getUser());
		properties.put("deleted", entity.isDeleted());
		properties.put("qname", entity.getQName().toString());		
		try {
			List<ModelField> fields = dictionaryService.getModelFields(entity.getQName());
			for(Property property : entity.getProperties()) {
				ModelField field = null;
				for(ModelField f : fields) {
					if(f.getQName().equals(property.getQName())) {
						field = f;
						break;
					}
				}
				if(field != null) {
					if(property.getValue() != null && !property.getValue().equals("")) {
						if(property.getValue() instanceof String) {
							properties.put(property.getQName().toString(), clean((String)property.getValue()));
						} else {
							properties.put(property.getQName().toString(), property.getValue());
						}
					}
				} 
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return map;
	}
	protected Map<String,Object> toMap(Association association) {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> properties = new HashMap<String,Object>();
		map.put("map", properties);
		try {
			Entity sourceEntity = getEntity(association.getSource());
			ModelRelation model = dictionaryService.getModelRelation(sourceEntity.getQName(), association.getQName());
			List<ModelField> fields = model.getFields();
			for(Property property : association.getProperties()) {
				ModelField field = null;
				for(ModelField f : fields) {
					if(f.getQName().equals(property.getQName())) {
						field = f;
						break;
					}
				}
				if(field != null) {
					if(property.getValue() != null) {
						if(property.getValue() instanceof String) {
							properties.put(property.getQName().toString(), clean((String)property.getValue()));
						} else {
							properties.put(property.getQName().toString(), property.getValue());
						}
					}
				}
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return map;
	}
	protected String clean(String in) {
		if(in.startsWith("<p>") && in.endsWith("</p>")) {
			in = in.substring(3, in.length()-4);
		}
		if(in.equals("&nbsp;") || in.equals("null")) return "";
		return in;
	}
		
	@Override
	public void index(Entity entity) {
		Model sourceModel = dictionaryService.getModel(entity.getQName());
		if(sourceModel.isSearchable()) {
			for(EntityPersistenceListener listener : persistenceListeners) {
				listener.onIndex(entity);
			}
		}
	}
	@Override
	public void index(List<Entity> entities) {
		for(Entity entity: entities) {
			Model sourceModel = dictionaryService.getModel(entity.getQName());
			if(sourceModel.isSearchable()) {
				for(EntityPersistenceListener listener : persistenceListeners) {
					listener.onIndex(entity);
				}
			}
		}		
	}

	@Override
	public void index(Long id) {
		Entity entity = getEntity(id);
		if(entity != null) index(entity);
		else {
			indexingService.removeIndexEntity(id);
			log.info("removing missing node from node index : "+id);
		}
	}
	public void registerImportProcessor(String name, ImportProcessor processor) {
		List<ImportProcessor> list = importers.get(name);
		if(list == null) {
			list = new ArrayList<ImportProcessor>();
			importers.put(name, list);
		}
		list.add(processor);
	}
	
	public List<ImportProcessor> getImportProcessors(String name) {
		List<ImportProcessor> processors = new ArrayList<ImportProcessor>();
		for(String key : importers.keySet()) {
			if(key.startsWith(name) || key.equals("default")) {
				List<ImportProcessor> procs = importers.get(key);
				processors.addAll(procs);
			}
		}
		return processors;
	}

	public void setImporters(Map<String, List<ImportProcessor>> importers) {
		this.importers = importers;
	}
	public void addPersistenceListener(EntityPersistenceListener listener) {
		persistenceListeners.add(listener);
	}
	public void setPersistenceListeners(List<EntityPersistenceListener> persistenceListeners) {
		this.persistenceListeners = persistenceListeners;
	}
	public void registerEntityPersistenceListener(String name, EntityPersistenceListener component) {
		persistenceListeners.add(component);
	}
}
