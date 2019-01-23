package org.archivemanager.services.search.indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRequest {
	private String template = "*";
	private Map<String,TemplateMapping> mappings = new HashMap<String,TemplateMapping>();
	
	
	public TemplateRequest() {
		TemplateMapping templateMapping = new TemplateMapping();
		List<Map<String,Object>> templates = new ArrayList<>();
		//mappings.put("_default_", templates);
		
	}
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
		
}
