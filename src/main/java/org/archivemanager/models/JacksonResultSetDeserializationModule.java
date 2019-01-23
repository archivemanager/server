package org.archivemanager.models;
import java.io.IOException;

import org.archivemanager.models.repository.Attribute;
import org.archivemanager.models.repository.Audio;
import org.archivemanager.models.repository.Breadcrumb;
import org.archivemanager.models.repository.Category;
import org.archivemanager.models.repository.Collection;
import org.archivemanager.models.repository.Entry;
import org.archivemanager.models.repository.Item;
import org.archivemanager.models.repository.NamedEntity;
import org.archivemanager.models.repository.Paging;
import org.archivemanager.models.repository.Result;
import org.archivemanager.models.repository.ResultSet;
import org.archivemanager.models.repository.Subject;
import org.archivemanager.models.repository.Video;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class JacksonResultSetDeserializationModule extends SimpleModule {
	private static final long serialVersionUID = 2474538804885591910L;
		
	
	public JacksonResultSetDeserializationModule() {
		addDeserializer(ResultSet.class, new ResultSetDeserializer());
	}
	
	public static class ResultSetDeserializer extends StdDeserializer<ResultSet> {
		private static final long serialVersionUID = 7142580498927076715L;
		private ObjectMapper mapper = new ObjectMapper();
		
		public ResultSetDeserializer() {
			this(null);
		}
		protected ResultSetDeserializer(Class<?> vc) {
			super(vc);
		}
		
		@Override
		public ResultSet deserialize(JsonParser p, DeserializationContext ctxt)	throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			ResultSet resultset = new ResultSet();
			if(node.has("query")) resultset.setQuery(node.get("query").asText());
			if(node.has("start")) resultset.setStart(node.get("start").asInt());
			if(node.has("end")) resultset.setEnd(node.get("end").asInt());
			if(node.has("resultCount")) resultset.setResultCount(node.get("resultCount").asInt());
			if(node.has("pageCount")) resultset.setPageCount(node.get("pageCount").asInt());
			if(node.hasNonNull("parent")) {
				JsonNode parentNode = node.get("parent");
				Result result = getResult(parentNode);					
				if(result != null)
					resultset.setParent(result);
			}
			ArrayNode results = (ArrayNode)node.get("results");
			if(results != null) {
				for(JsonNode resultNode : results) {
					Result result = getResult(resultNode);					
					if(result != null)
						resultset.getResults().add(result);
				}				
			}
			ArrayNode pages = (ArrayNode)node.get("paging");
			if(pages != null) {
				for(JsonNode resultNode : pages) {
					Paging page = mapper.treeToValue(resultNode, Paging.class);
					resultset.getPaging().add(page);
				}
			}
			ArrayNode attributes = (ArrayNode)node.get("attributes");
			if(attributes != null) {
				for(JsonNode resultNode : attributes) {
					Attribute attribute = mapper.treeToValue(resultNode, Attribute.class);
					resultset.getAttributes().add(attribute);
				}
			}
			ArrayNode breadcrumbs = (ArrayNode)node.get("breadcrumbs");
			if(breadcrumbs != null) {
				for(JsonNode resultNode : breadcrumbs) {
					Breadcrumb breadcrumb = mapper.treeToValue(resultNode, Breadcrumb.class);
					resultset.getBreadcrumbs().add(breadcrumb);
				}
			}
			return resultset;
		}
		protected Result getResult(JsonNode resultNode) throws JsonProcessingException {
			String contentType = resultNode.get("contentType").asText();
			Result result = null;
			if(contentType.equals("collection"))
				result = mapper.treeToValue(resultNode, Collection.class);
			else if(contentType.equals("entry"))
				result = mapper.treeToValue(resultNode, Entry.class);
			else if(contentType.equals("person") || contentType.equals("corporation"))
				result = mapper.treeToValue(resultNode, NamedEntity.class);
			else if(contentType.equals("subject"))
				result = mapper.treeToValue(resultNode, Subject.class);
			else if(contentType.equals("category"))
				result = mapper.treeToValue(resultNode, Category.class);
			else if(contentType.equals("video"))
				result =  mapper.treeToValue(resultNode, Video.class);
			else if(contentType.equals("audio"))
				result =  mapper.treeToValue(resultNode, Audio.class);
			else 
				result = mapper.treeToValue(resultNode, Item.class);
			if(result != null) {
				if(resultNode.has("user"))
					result.setUser(resultNode.get("user").asLong());			
			}
			return result;
		}
	}	
}
