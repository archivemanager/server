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

import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.search.Definition;
import org.archivemanager.services.search.DictionaryPlugin;
import org.archivemanager.services.search.dictionary.QNameDefinition;


public class QNameDictionaryPlugin implements DictionaryPlugin {
	private DataDictionaryService dictionaryService;
	private String entityName;
	private int limit;
	
	
	
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		try {
			QName entityQname = QName.createQualifiedName(entityName);
			List<Model> models = dictionaryService.getChildModels(entityQname);
			if(models != null && models.size() > 0) {
				for(Model child : models) {
					defs.add(new QNameDefinition(child.getQName().getLocalName(), "localName:"+child.getQName().getLocalName()));
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return defs;
	}
	
	public String getEntity() {
		return entityName;
	}
	public void setEntity(String entityName) {
		this.entityName = entityName;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
		
}
