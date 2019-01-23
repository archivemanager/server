package org.archivemanager.services.search.dictionary;

import org.archivemanager.services.search.Token;
import org.archivemanager.services.search.dictionary.DefinitionSupport;

public class NumericDefinition extends DefinitionSupport {

	
	public NumericDefinition(String value) {
		super(Token.NUMB, value, value);
	}
	@Override
	public String toString() {
		return "Number";
	}
}