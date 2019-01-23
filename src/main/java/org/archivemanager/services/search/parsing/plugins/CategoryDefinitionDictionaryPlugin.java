/*
 * Copyright (C) 2010 Heed Technology Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.archivemanager.services.search.parsing.plugins;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.services.search.Definition;
import org.archivemanager.services.search.DictionaryPlugin;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.search.dictionary.CategoryDefinition;
import org.archivemanager.util.StringUtility;


public class CategoryDefinitionDictionaryPlugin implements DictionaryPlugin {
	private SearchService searchService;
	private int minLength = 1;
	
	
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		/*
		Query seriesQuery = new TermQuery(new Term(ContentModel.COMPONENT_LEVEL.getLocalName(), "series"));
		Query subseriesQuery = new TermQuery(new Term(ContentModel.COMPONENT_LEVEL.getLocalName(), "subseries"));
		Query fileQuery = new TermQuery(new Term(ContentModel.COMPONENT_LEVEL.getLocalName(), "file"));
		BooleanQuery query = new BooleanQuery();
		if(level.equals("all")) {			
			query.add(seriesQuery, Occur.SHOULD);
			query.add(subseriesQuery, Occur.SHOULD);
			query.add(fileQuery, Occur.SHOULD);
		} else if(level.equals("series")) query.add(seriesQuery, Occur.MUST);
		else if(level.equals("subseries")) query.add(subseriesQuery, Occur.MUST);
		else if(level.equals("file")) query.add(subseriesQuery, Occur.MUST);
		*/
		SearchRequest query = new SearchRequest(RepositoryModel.CATEGORY, null, "name", "asc");
		query.setEndRow(0);
		try {
			SearchResponse categoryDocs = searchService.search(query);
			System.out.println(categoryDocs.getResults().size()+" categories loading...");
			Map<String,List<String>> categories = new HashMap<String,List<String>>();
			for(SearchResult category : categoryDocs.getResults()) {
				SearchRequest entityRequest = new SearchRequest();
				entityRequest.setNodeId(Long.valueOf(category.getId()));
				SearchResponse entityResponse = searchService.search(entityRequest);
				SearchResult result = entityResponse.getResult(0);
				String title = result.getName();
				if(title != null && title.length() >= minLength) {
					title = StringUtility.removeTags(title).toLowerCase().replace("\"", "");
					//String value = title.replace(",", "").replace("-", "").replace("+", "").replace(".", "").replace("(", "").replace(")", "");
					if(categories.containsKey(title)) categories.get(title).add(String.valueOf(category.getId()));
					else {
						List<String> ids = new ArrayList<String>();
						ids.add(String.valueOf(category.getId()));
						categories.put(title, ids);
					}
				}
			}
			for(String value : categories.keySet()) {
				CategoryDefinition def = new CategoryDefinition(value, value);
				def.setIds(categories.get(value));
				defs.add(def);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return defs;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
		
}
