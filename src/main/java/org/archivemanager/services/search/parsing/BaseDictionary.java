package org.archivemanager.services.search.parsing;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.search.Definition;
import org.archivemanager.services.search.Dictionary;
import org.archivemanager.services.search.DictionaryPlugin;
import org.archivemanager.services.search.parsing.plugins.QNameDictionaryPlugin;


public class BaseDictionary implements Dictionary {
	protected List<Definition> definitions = new ArrayList<Definition>();
	protected List<DictionaryPlugin> plugins = new ArrayList<DictionaryPlugin>();
	
	public static final String PROPERTY_VALUE_DEFINITION = "property_value";
	public static final String QNAME_DEFINITION = "qname";
	
	
	public Definition lookup(int type, String name) {
		for(Definition def : definitions)
			if(def.getType() == type && def.getName().equals(name)) 
				return def;
		return null;
	}
	
	public void addDefinition(Definition definition) {
		definitions.add(definition);
	}
	public void addDefinition(String type, String name, String value) {
		if(type.equals(PROPERTY_VALUE_DEFINITION)) {
			//PropertyValueDictionaryPlugin plugin = new PropertyValueDictionaryPlugin(searchService);
			//plugin.setDictionary(dictionary);
			//plugin.setEntity(RepositoryModel.ITEM.toString());
			//plugin.setProperty(value);
			//plugins.add(plugin);
		} else if(type.equals(QNAME_DEFINITION)) {
			QNameDictionaryPlugin plugin = new QNameDictionaryPlugin();
			plugin.setEntity(value);
			plugins.add(plugin);
		} 
	}	
	public void addPlugin(DictionaryPlugin plugin) {
		plugins.add(plugin);
	}
	public void setPlugins(List<DictionaryPlugin> plugins) {
		this.plugins = plugins;
	}
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		for(DictionaryPlugin plugin : plugins) {
			defs.addAll(plugin.getDefinitions());
		}
		defs.addAll(definitions);
		return defs;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}
	
}
