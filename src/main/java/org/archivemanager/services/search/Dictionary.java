package org.archivemanager.services.search;
import java.util.List;


public interface Dictionary {

	Definition lookup(int type, String name);
	void addDefinition(Definition definition);
	void addDefinition(String type, String name, String value);
	List<Definition> getDefinitions();
	void addPlugin(DictionaryPlugin plugin);
	
}
