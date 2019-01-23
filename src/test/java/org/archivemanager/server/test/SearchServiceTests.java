package org.archivemanager.server.test;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import org.archivemanager.data.RestResponse;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.system.QName;


public class SearchServiceTests extends TestSupport {

/*
	@SuppressWarnings("rawtypes")
	@Test
	public void textSearch() throws Exception {
		EntityQuery query = new EntityQuery();
		query.setType(EntityQuery.TYPE_SEARCH);
		query.setQueryString("king");
		query.setEntityQnames(new QName[] {RepositoryModel.COLLECTION});
		//String queryStr = getObjectMapper().writeValueAsString(query);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<EntityQuery> httpEntity = new HttpEntity<EntityQuery>(query, headers);
		ListenableFuture<ResponseEntity<RestResponse>> future = getRestTemplate().postForEntity("http://localhost:9000/service/search/entity.json", httpEntity, RestResponse.class, headers);
		ResponseEntity<RestResponse> response = future.get();
		assertTrue(response.getStatusCode().is2xxSuccessful());
	}
	@SuppressWarnings("rawtypes")
	@Test
	public void prefixSearch() throws Exception {
		EntityQuery query = new EntityQuery();
		query.setType(EntityQuery.TYPE_SEARCH);
		query.setQueryString("a*");
		query.setFields(new String[] {"openapps_org_system_1_0_name"});
		query.setEntityQnames(new QName[] {RepositoryModel.COLLECTION});
		//String queryStr = getObjectMapper().writeValueAsString(query);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<EntityQuery> httpEntity = new HttpEntity<EntityQuery>(query, headers);
		ListenableFuture<ResponseEntity<RestResponse>> future = getRestTemplate().postForEntity("http://127.0.0.1:9000/service/search/entity.json", httpEntity, RestResponse.class, headers);
		ResponseEntity<RestResponse> response = future.get();
		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertTrue(response.getBody().getResponse().getTotalRows() > 0);
	}
	@Test
	public void jsonSerialization() throws Exception {
		ObjectMapper mapper = getObjectMapper();

		EntityQuery query = new EntityQuery();
		query.setType(EntityQuery.TYPE_SEARCH);
		query.setQueryString("a*");
		query.setFields(new String[] {"openapps_org_system_1_0_name_e"});
		query.setEntityQnames(new QName[] {RepositoryModel.COLLECTION});

		String json = mapper.writeValueAsString(query);
		System.out.print(json);
	}
*/
}
