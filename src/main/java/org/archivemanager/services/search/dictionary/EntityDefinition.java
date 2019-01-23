package org.archivemanager.services.search.dictionary;

import org.archivemanager.services.search.Token;

public class EntityDefinition extends DefinitionSupport {

	
	public EntityDefinition(String name, String value) {
		super(Token.ENTITY, name, value);
	}
}
