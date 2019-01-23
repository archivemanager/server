package org.archivemanager.services.search.indexing;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.archivemanager.models.SearchModel;
import org.archivemanager.services.search.IndexCreationRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

//import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RemoteElasticSearchIndexingService implements IndexingService {
	private final static Logger log = Logger.getLogger(RemoteElasticSearchIndexingService.class.getName());
	@Autowired private RestHighLevelClient client;
	@Autowired private RestClient restClient;
	@Autowired private ObjectMapper objMapper;
	
	
	
	@PostConstruct
	public void initialize() {		
		try {
			Map<String, String> params = Collections.emptyMap();
			try {				
				IndexCreationRequest request = new IndexCreationRequest();
				FieldMapping eventMapping = new FieldMapping();
				eventMapping.addField("qname", "text", true, true);
				request.getMappings().put("node", eventMapping);
				Response response = restClient.performRequest("PUT", "/nodes", params, new NStringEntity(objMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
				log.info("nodes index initialized status : "+response.getStatusLine().getStatusCode()+", reason : "+response.getStatusLine().getReasonPhrase());
			} catch (ResponseException e) {
				log.info("index initialization failure [nodes] "+e.getMessage());
		    }
			try {				
				IndexCreationRequest request = new IndexCreationRequest();
				FieldMapping eventMapping = new FieldMapping();
				eventMapping.addField("qname", "text", true, true);
				eventMapping.addField("sourceName", "text", true, true);
				eventMapping.addField("targetName", "text", true, true);
				request.getMappings().put("association", eventMapping);
				Response response = restClient.performRequest("PUT", "/associations", params, new NStringEntity(objMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
				log.info("nodes index initialized status : "+response.getStatusLine().getStatusCode()+", reason : "+response.getStatusLine().getReasonPhrase());
			} catch (ResponseException e) {
				log.info("index initialization failure [nodes] "+e.getMessage());
		    }
			try {				
				//restClient.performRequest("PUT", "/events");
				IndexCreationRequest request = new IndexCreationRequest();
				FieldMapping eventMapping = new FieldMapping();
				eventMapping.addField("qname", "text", true, true);
				eventMapping.addField("openapps_org_system_1_0_timestamp", "long", false, true);
				request.getMappings().put("event", eventMapping);
				Response response = restClient.performRequest("PUT", "/events", params, new NStringEntity(objMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
				log.info("events index initialized status : "+response.getStatusLine().getStatusCode()+", reason : "+response.getStatusLine().getReasonPhrase());
			} catch (ResponseException e) {
				log.info("index initialization failure [events] "+e.getMessage());   
		    }
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void indexEntity(IndexEntity entity) {		
		try {
			IndexRequest request = getIndexRequest(entity);
			client.index(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	@Override
	public void indexEntities(List<IndexEntity> entities) {		
		try {
			BulkRequest request = new BulkRequest();
			for(IndexEntity entity : entities) {
				request.add(getIndexRequest(entity));
			}
			client.bulk(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	
	@Override
	public void indexAssociation(IndexAssociation association) {
		try {
			IndexRequest request = getIndexRequest(association);
			client.index(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	@Override
	public void indexAssociations(List<IndexAssociation> associations) {
		try {
			BulkRequest request = new BulkRequest();
			for(IndexAssociation association : associations) {
				IndexRequest indexRequest = getIndexRequest(association);
				request.add(indexRequest);
			}
			client.bulk(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	@Override
	public void removeIndexEntity(Long id) {
		DeleteRequest request = new DeleteRequest("nodes", "node", String.valueOf(id));
		try {
			client.delete(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	@Override
	public void removeIndexAssociation(Long id) {
		DeleteRequest request = new DeleteRequest("associations", "association", String.valueOf(id));
		try {
			client.delete(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	
	protected IndexRequest getIndexRequest(IndexEntity entity) {
		IndexRequest request = entity.getQnames().contains(SearchModel.EVENT) ?
				new IndexRequest("events", "event") :
				new IndexRequest("nodes", "node");
		if(entity.getId() != null && entity.getId() > 0) 
			request.id(String.valueOf(entity.getId()));
		Map<String, Object> jsonMap = new HashMap<>();
		for(IndexField field : entity.getFields()) {
			jsonMap.put(field.getName(), field.getValue());
		}				
		request.source(jsonMap);
		request.opType(IndexRequest.OpType.INDEX);
		return request;
	}
	protected IndexRequest getIndexRequest(IndexAssociation association) {
		IndexRequest request = new IndexRequest("associations", "association");
		if(association.getId() != null && association.getId() > 0) 
			request.id(String.valueOf(association.getId()));
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("qname", association.getQName().toString());
		for(IndexField field : association.getFields()) {
			jsonMap.put(field.getName(), field.getValue());
		}				
		request.source(jsonMap);
		request.opType(IndexRequest.OpType.INDEX);
		return request;
	}	
}
