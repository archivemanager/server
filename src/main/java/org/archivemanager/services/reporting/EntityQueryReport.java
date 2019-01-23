package org.archivemanager.services.reporting;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.archivemanager.data.IDName;
import org.archivemanager.data.Record;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.util.StringFormatUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EntityQueryReport extends Report {
	@Autowired private SearchService searchService;
	
	
	public EntityQueryReport() {
		super("entity_query", "Entity Query");
	}

	@Override
	public RestResponse<Record> generate(SearchRequest request) {
		RestResponse<Record> records = new RestResponse<Record>();
				
		SearchResponse response = searchService.search(request);
		Map<String,String> columnMap = new HashMap<String,String>(); 			
		for(SearchResult result : response.getResults()) {
			for(String key : result.getData().keySet()) {
				if(!key.endsWith("_") && !key.equals("qname") && !key.equals("acl") && !key.equals("source_assoc") && 
						!key.equals("target_assoc") && !key.equals("sort_") && !key.equals("created") && 
						!key.equals("deleted") && !key.equals("user") && !key.equals("modified") && 
						!key.equals("parent_id") && !key.equals("parent_qname") && !key.equals("target_assoc")) {
					if(!columnMap.containsKey(key)) {
						columnMap.put(key, format(key));
					}
				}
			}
		}
		List<String> columns = columnMap.values().stream().sorted((o1, o2)->o1.compareTo(o2)).collect(Collectors.toList());
		for(SearchResult result : response.getResults()) {
			Record list = new Record();
			list.add(new IDName("ID", result.getId().toString()));
			list.add(new IDName("Name", result.getName()));
			list.add(new IDName("QName", result.getQName().toString()));
			for(String name : columns) {
				for(String key : columnMap.keySet()) {
					if(columnMap.get(key).equals(name)) {
						Object value = result.getData().get(key);
						if(value != null) {
							list.add(new IDName(name, value.toString()));
						} else list.add(new IDName(name, ""));
					}
				}
			}
			records.addRow(list);
		}
		records.setTotal(response.getResultSize());
		return records;
	}
	protected String format(String in) {
		if(in == null || in.length() == 0) return "";
		QName qname = new QName(in);
		return StringFormatUtility.toTitleCase(qname.getLocalName().replace("_", " "));
	}
}
