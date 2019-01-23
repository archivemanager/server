package org.archivemanager;

import org.apache.http.HttpHost;
import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.data.Sort;
import org.archivemanager.models.JacksonQNameModule;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.services.entity.AssociationSorter;
import org.archivemanager.services.entity.EntityService;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


@SpringBootApplication
@EnableConfigurationProperties({PropertyConfiguration.class})
public class ArchiveManagerServer {
	@Autowired private PropertyConfiguration properties;
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.registerModule(new JacksonQNameModule());
		return mapper;
	}
	@Bean
	public AssociationSorter getAssociationSorter(EntityService entityService) {
		AssociationSorter assocSort = new AssociationSorter(entityService, new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "desc"));		
		return assocSort;
	}
	
	@Bean
	public RestClient getElasticSearchRestClient() {
		RestClientBuilder builder = RestClient.builder(new HttpHost(properties.getSearchHost(), 9200, "http"));
		builder.setFailureListener(new RestClient.FailureListener() {
			public void onFailure(HttpHost host) {

	        }
		});
		return builder.build();
	}
	@Bean
	public RestHighLevelClient getElasticsearchClient() {
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(properties.getSearchHost(), properties.getSearchPort(), "http")));
		return client;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ArchiveManagerServer.class, args);
	}
}
