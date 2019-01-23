package org.archivemanager.server.test;
import java.util.HashMap;
import java.util.Map;

import org.archivemanager.data.RestRequest;
import org.archivemanager.data.RestResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import static org.junit.Assert.*;


public class EntityServiceTests extends TestSupport {
	
	
	@SuppressWarnings("rawtypes")
	//@Test
	public void fetchEntityByIdTest() throws Exception {		
		Map<String,Object> headers = new HashMap<String,Object>();
		//DeferredResult result = new DeferredResult(5000L);
		ListenableFuture<ResponseEntity<RestResponse>> future = getRestTemplate().getForEntity("http://localhost:9000/service/entity/get.json?id=22", RestResponse.class, headers);
		ResponseEntity<RestResponse> response = future.get();
		assertTrue(response.getStatusCode().is2xxSuccessful());
		
		ListenableFuture<ResponseEntity<RestResponse>> future2 = getRestTemplate().getForEntity("http://localhost:9000/service/entity/get/22.json", RestResponse.class, headers);
		ResponseEntity<RestResponse> response2 = future2.get();
		assertTrue(response2.getStatusCode().is2xxSuccessful());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void addEntityTest() throws Exception {
		RestRequest request = new RestRequest();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<RestRequest> httpEntity = new HttpEntity<RestRequest>(request, headers);
		ListenableFuture<ResponseEntity<RestResponse>> future2 = getRestTemplate().postForEntity("http://localhost:9000/service/entity/add.json", httpEntity, RestResponse.class, headers);
		ResponseEntity<RestResponse> response2 = future2.get();
		assertTrue(response2.getStatusCode().is2xxSuccessful());
	}
	
}
