package org.archivemanager.services.search.dictionary;

import org.archivemanager.services.search.Token;

public class AllResultsDefinition extends DefinitionSupport {

	
	public AllResultsDefinition(String name) {
		super(Token.ALL, name, null);
	}
	
	@Override
	public String toString() {
		return "AllResults";
	}
}
