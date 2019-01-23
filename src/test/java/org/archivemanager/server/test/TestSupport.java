package org.archivemanager.server.test;

import java.util.Arrays;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestSupport {
	private static AsyncRestTemplate restTemplate;
	private static ObjectMapper objectMapper;
	
	
	public static AsyncRestTemplate getRestTemplate() {
		if(restTemplate == null) {
			CloseableHttpAsyncClient client = HttpAsyncClients.custom().disableCookieManagement()
					.disableAuthCaching()
					.setUserAgent("ArchiveManager")
					.setMaxConnTotal(1000)
					.setMaxConnPerRoute(1000)
					.setDefaultRequestConfig(RequestConfig.custom()
							.setConnectTimeout(30000)
							.setSocketTimeout(30000)
							.setAuthenticationEnabled(false)
							.setContentCompressionEnabled(true)
							.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
							.build())
					.build();
			HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(client);
			restTemplate = new AsyncRestTemplate(factory);
			restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(getObjectMapper())));
		}
		return restTemplate;
	}
	public static ObjectMapper getObjectMapper() {
		if(objectMapper == null) {
			objectMapper = new ObjectMapper()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				//.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());			
		}
		return objectMapper;
	}
}
