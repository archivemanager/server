package org.archivemanager.models;

import org.archivemanager.models.system.QName;


public class ContactModel {
	public static final String CONTACT_MODEL_1_0_URI = "openapps_org_contact_1_0";
	
	public static final QName SALUTATION = new QName(CONTACT_MODEL_1_0_URI, "salutation");
	public static final QName CONTACT = new QName(CONTACT_MODEL_1_0_URI, "contact");
	public static final QName CONTACTS = new QName(CONTACT_MODEL_1_0_URI, "contacts");
	public static final QName INDIVIDUAL = new QName(CONTACT_MODEL_1_0_URI, "individual");
	public static final QName ORGANIZATION = new QName(CONTACT_MODEL_1_0_URI, "organization");
	public static final QName ADDRESSES = new QName(CONTACT_MODEL_1_0_URI, "addresses");
	public static final QName ADDRESS = new QName(CONTACT_MODEL_1_0_URI, "address");
	public static final QName ADDRESS1 = new QName(CONTACT_MODEL_1_0_URI, "address1");
	public static final QName ADDRESS2 = new QName(CONTACT_MODEL_1_0_URI, "address2");
	public static final QName CITY = new QName(CONTACT_MODEL_1_0_URI, "city");
	public static final QName STATE = new QName(CONTACT_MODEL_1_0_URI, "state");
	public static final QName COUNTRY = new QName(CONTACT_MODEL_1_0_URI, "country");
	public static final QName ZIP = new QName(CONTACT_MODEL_1_0_URI, "zip");
	
	public static final QName PHONE = new QName(CONTACT_MODEL_1_0_URI, "phone");
	public static final QName PHONES = new QName(CONTACT_MODEL_1_0_URI, "phones");
	public static final QName NUMBER = new QName(CONTACT_MODEL_1_0_URI, "number");
	public static final QName FAX = new QName(CONTACT_MODEL_1_0_URI, "fax1");
	public static final QName EMAIL = new QName(CONTACT_MODEL_1_0_URI, "email");
	public static final QName EMAILS = new QName(CONTACT_MODEL_1_0_URI, "emails");
	
	public static final QName FIRST_NAME = new QName(CONTACT_MODEL_1_0_URI, "first_name");
	public static final QName LAST_NAME = new QName(CONTACT_MODEL_1_0_URI, "last_name");
	public static final QName MIDDLE_NAME = new QName(CONTACT_MODEL_1_0_URI, "middle_name");
	public static final QName TITLE = new QName(CONTACT_MODEL_1_0_URI, "title");
	public static final QName GREETING = new QName(CONTACT_MODEL_1_0_URI, "greeting");
	public static final QName SUFFIX = new QName(CONTACT_MODEL_1_0_URI, "suffix");
	
	public static final QName BIRTHDATE = new QName(CONTACT_MODEL_1_0_URI, "birth_date");
	public static final QName DEATHDATE = new QName(CONTACT_MODEL_1_0_URI, "death_date");
	public static final QName LASTSHIP = new QName(CONTACT_MODEL_1_0_URI, "last_ship");
	public static final QName LASTWROTE = new QName(CONTACT_MODEL_1_0_URI, "last_wrote");
	
	public static final QName RELATIONSHIP = new QName(CONTACT_MODEL_1_0_URI, "relationship");
	public static final QName TYPE = new QName(CONTACT_MODEL_1_0_URI, "type");
	public static final QName STATUS = new QName(CONTACT_MODEL_1_0_URI, "status");
	public static final QName SPOUSE = new QName(CONTACT_MODEL_1_0_URI, "spouse");
	public static final QName NOTE = new QName(CONTACT_MODEL_1_0_URI, "note");
	public static final QName BIOGRAPHY = new QName(CONTACT_MODEL_1_0_URI, "biography");
	public static final QName BIO_SOURCES = new QName(CONTACT_MODEL_1_0_URI, "bio_sources");
	public static final QName DATELIST = new QName(CONTACT_MODEL_1_0_URI, "datelist");
	public static final QName ROLE = new QName(CONTACT_MODEL_1_0_URI, "role");
}
