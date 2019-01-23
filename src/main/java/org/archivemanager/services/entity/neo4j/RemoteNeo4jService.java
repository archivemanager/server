package org.archivemanager.services.entity.neo4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.system.QName;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RemoteNeo4jService {
	private final static Logger log = Logger.getLogger(RemoteNeo4jService.class.getName());
	@Autowired private PropertyConfiguration properties;
	//@Autowired private NodePersistenceListener[] listeners;
	
	private Driver driver;	
	
	
	@PostConstruct
	public void initialize() {
		driver = GraphDatabase.driver( "bolt://"+properties.getDatastoreHost(), AuthTokens.basic(properties.getDatastoreUsername(), properties.getDatastorePassword()));
	}

	public Node getNode(long id) {
		Session session = session();
		try {
			StatementResult result = session.run("MATCH (n) WHERE ID(n) = "+id+" return n;");
			org.neo4j.driver.v1.types.Node n = null;
			while(result.hasNext()) {
				Record record = result.next();				
				n = record.get("n").asNode();
			}
			return n;
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return null;
	}
	public List<Relationship> getRelations(long id) {
		List<Relationship> relationships = new ArrayList<Relationship>();
		Session session = session();
		try {
			StatementResult result = session.run("MATCH (n)-[r] WHERE ID(n) = "+id+" return r;");
			org.neo4j.driver.v1.types.Relationship r = null;
			while(result.hasNext()) {
				Record record = result.next();				
				r = record.get("r").asRelationship();
				relationships.add(r);
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return relationships;
	}
	public List<Long> getNodes(QName qname) {
		List<Long> nodes = new ArrayList<Long>();
		Session session = session();
		String query = "MATCH (n) WHERE n.qname='"+qname.toString()+"' return n";
		try {			
			StatementResult mainResult = session.run(query);			
			org.neo4j.driver.v1.types.Node n = null;
			while(mainResult.hasNext()) {
				Record record = mainResult.next();
				n = record.get("n").asNode();
				nodes.add(n.id());					
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return nodes;
	}
	public void save(long id, Map<String,Object> map) {
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("map", map);
		Session session = session();
		try {
			session.run("MATCH (n) WHERE ID(n) = "+id+" SET n=$map return n;", properties);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
	}
	public void addRelation(QName qname, long start, long end) {
		Session session = session();
		try {
			String query = "MATCH (n1),(n2) WHERE id(n1) = "+start+" and id(n2) = "+end;
			query += " CREATE (n1)-[r:"+qname.toString()+"]->(n2) return r;";
			session.run(query);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		
	}
	public void removeNode(long id) {
		Session session = session();
		try {
			session.run("MATCH (n) WHERE ID(n)=" + id + " DETACH DELETE n");
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}		
	}
	public void removeRelation(long id) {
		Session session = session();
		try {
			session.run("MATCH ()-[r]-() WHERE ID(r)=" + id + " DELETE r");
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}		
	}
	public Session session() {
		return driver.session();		
	}
	public void shutdown() {
		driver.close();
	}
	
}
