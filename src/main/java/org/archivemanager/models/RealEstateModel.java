package org.archivemanager.models;

import org.archivemanager.models.system.QName;


public class RealEstateModel {
	public static final String NAMESPACE_ARCHIVE = "openapps_org_realestate_1_0";
	
	public static final QName PROPERTY = new QName(NAMESPACE_ARCHIVE, "property");
	
	public static final QName MLS_ID = new QName(NAMESPACE_ARCHIVE, "mls_id");
	public static final QName LIST_PRICE = new QName(NAMESPACE_ARCHIVE, "list_price");
	public static final QName LOT_SIZE = new QName(NAMESPACE_ARCHIVE, "lot_size");
	public static final QName HOME_SIZE = new QName(NAMESPACE_ARCHIVE, "home_size");
}
