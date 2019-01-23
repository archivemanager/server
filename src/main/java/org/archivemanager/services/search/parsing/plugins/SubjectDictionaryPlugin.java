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
import java.util.List;

import org.archivemanager.models.ClassificationModel;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.search.Definition;
import org.archivemanager.services.search.DictionaryPlugin;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.search.dictionary.SubjectDefinition;


public class SubjectDictionaryPlugin implements DictionaryPlugin {
	private SearchService searchService;
	private EntityService entityService;
	
	
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		try {
			SearchResponse subjects = searchService.search(new SearchRequest(ClassificationModel.SUBJECT, null, "name", "desc"));
			if(subjects != null) {
				for(SearchResult subject : subjects.getResults()) {
					SubjectDefinition def = new SubjectDefinition(entityService.getEntity(subject.getId()).getName(), String.valueOf(subject.getId()));
					defs.add(def);
					/*
					String search_values = name.getProperty(ClassificationModel.SUBJECT_SEARCH_VALUES).toString();
					if(search_values != null && !search_values.equals("")) {
						String[] values = search_values.split(",");
						for(int i=0; i < values.length; i++) {
							NamedEntityDefinition def = new NamedEntityDefinition(values[i].trim(), String.valueOf(id));
							defs.add(def);
						}
					}
					*/
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return defs;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
}
