package org.archivemanager.services.search.dictionary;



public class DateDefinition extends DefinitionSupport {

	
	public DateDefinition(int type, String field, int value) {
		super(type, String.valueOf(value), field+":"+String.valueOf(value));		
	}
	public DateDefinition(int type, String field, String value) {
		super(type, String.valueOf(value), field+":"+value);		
	}
}
