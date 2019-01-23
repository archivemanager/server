package org.archivemanager.services.scheduling;

public enum Period {
	MINUTE("Minute"),
	HOUR("Hour"),
	DAY("Day"),
	WEEK("Week"),
	MONTH("Month"),
	YEAR("Year");
	
	@SuppressWarnings("unused")
	private final String description;
	Period(String description) {
		this.description = description;
	}
}
