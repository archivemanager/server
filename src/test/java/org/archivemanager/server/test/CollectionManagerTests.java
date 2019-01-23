package org.archivemanager.server.test;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;

import org.archivemanager.data.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public class CollectionManagerTests extends TestSupport {
	


	@SuppressWarnings("rawtypes")
	//@Test
	public void noQuerySearch() throws Exception {
		Map<String,Object> headers = new HashMap<String,Object>();		
		String url = "http://localhost:9000/service/archivemanager/collection/fetch.json?api_key=&sources=true&parent=null&_operationType=fetch&_operationId=collectionTreeJsonDS_fetch&_textMatchStyle=exact&_componentId=isc_CollectionTree_0&_dataSource=collectionTreeJsonDS&isc_metaDataPrefix=_&isc_dataFormat=json";
		ListenableFuture<ResponseEntity<RestResponse>> future = getRestTemplate().getForEntity(url, RestResponse.class, headers);
		ResponseEntity<RestResponse> response = future.get();
		assertTrue(response.getStatusCode().is2xxSuccessful());
	}
	
	@SuppressWarnings("rawtypes")
	//@Test
	public void parentSearch() throws Exception {
		Map<String,Object> headers = new HashMap<String,Object>();		
		String url = "http://localhost:9000/service/archivemanager/collection/fetch.json?api_key=&sources=true&parent=11&_operationType=fetch&_operationId=collectionTreeJsonDS_fetch&_textMatchStyle=exact&_componentId=isc_CollectionTree_0&_dataSource=collectionTreeJsonDS&isc_metaDataPrefix=_&isc_dataFormat=json";
		ListenableFuture<ResponseEntity<RestResponse>> future = getRestTemplate().getForEntity(url, RestResponse.class, headers);
		ResponseEntity<RestResponse> response = future.get();
		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertTrue(response.getBody().getTotal() > 0);
	}
}
