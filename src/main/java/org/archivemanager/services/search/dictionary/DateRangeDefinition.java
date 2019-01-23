package org.archivemanager.services.search.dictionary;

import org.archivemanager.services.search.TokenTypes;

public class DateRangeDefinition extends DefinitionSupport {
	private int dataType;
	
	public static final int TYPE_EPOCH = 1;
	public static final int TYPE_YMD = 2;
	
	
	public DateRangeDefinition(int dataType, String name, String value) {
		super(TokenTypes.DRNG, name, value);
		this.dataType = dataType;
	}

	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
}
