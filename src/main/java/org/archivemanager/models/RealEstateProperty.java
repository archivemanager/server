package org.archivemanager.models;

import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.crawling.Document;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.Property;


public class RealEstateProperty extends Document {
	private static final long serialVersionUID = 2685749718987743287L;
	
	
	public RealEstateProperty() {
		setQName(RealEstateModel.PROPERTY);
	}
	public RealEstateProperty(Entity entity) {
		super(entity);
		setQName(RealEstateModel.PROPERTY);
	}
	public RealEstateProperty(String url, String name, String summary) {
		super(url,name,summary);
		setQName(RealEstateModel.PROPERTY);
	}
	
	public String getMLSId() {
		String price = getPropertyValueString(RealEstateModel.MLS_ID);
		if(price != null && price.length() > 0) return price;
		return null;
	}
	public void setMLSId(String id) {
		try {
			addProperty(RealEstateModel.MLS_ID, id);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+RealEstateModel.MLS_ID+" -> "+id);
		}
	}
	public int getPrice() {
		Property size = getProperty(RealEstateModel.LIST_PRICE);
		return (Integer)size.getValue();
	}
	public void setPrice(int price) {
		try {
			addProperty(RealEstateModel.LIST_PRICE, price);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+RealEstateModel.LIST_PRICE+" -> "+price);
		}
	}
	public int getHomeSize() {
		Property size = getProperty(RealEstateModel.HOME_SIZE);
		return (Integer)size.getValue();
	}
	public void setHomeSize(int size) {
		try {
			addProperty(RealEstateModel.HOME_SIZE, size);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+RealEstateModel.HOME_SIZE+" -> "+size);
		}
	}
	public double getLotSize() {
		Property size = getProperty(RealEstateModel.LOT_SIZE);
		return (Integer)size.getValue();
	}
	public void setLotSize(double size) {
		try {
			addProperty(RealEstateModel.LOT_SIZE, size);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+RealEstateModel.LOT_SIZE+" -> "+size);
		}
	}
	
	public static ModelField getPriceModel(String label, QName qname) {
		ModelField field = new ModelField(ModelField.TYPE_INTEGER,label,qname);
		field.setMaxSize(100);
		field.getValues().add(new ModelFieldValue("$100,000","100000",""));
		field.getValues().add(new ModelFieldValue("$200,000","200000",""));
		field.getValues().add(new ModelFieldValue("$300,000","300000",""));
		field.getValues().add(new ModelFieldValue("$400,000","400000",""));
		field.getValues().add(new ModelFieldValue("$500,000","500000",""));
		field.getValues().add(new ModelFieldValue("$600,000","600000",""));
		field.getValues().add(new ModelFieldValue("$700,000","700000",""));
		field.getValues().add(new ModelFieldValue("$800,000","800000",""));
		field.getValues().add(new ModelFieldValue("$900,000","900000",""));
		field.getValues().add(new ModelFieldValue("$1M","1000000",""));
		field.getValues().add(new ModelFieldValue("$1.5M","1500000",""));
		field.getValues().add(new ModelFieldValue("$2M","2000000",""));
		field.getValues().add(new ModelFieldValue("$2.5M","2500000",""));
		field.getValues().add(new ModelFieldValue("$3M","3000000",""));
		field.getValues().add(new ModelFieldValue("$3.5M","3500000",""));
		field.getValues().add(new ModelFieldValue("$4M","4000000",""));
		field.getValues().add(new ModelFieldValue("$4.5M","4500000",""));
		field.getValues().add(new ModelFieldValue("$5M","5000000",""));
		field.getValues().add(new ModelFieldValue("$10M","10000000",""));
		field.getValues().add(new ModelFieldValue("$20M","20000000",""));		
	    return field;
	}
	public static String getPriceLabel(String in) {
		if(in.equals("100000")) return "$100K";
		if(in.equals("200000")) return "$200K";
		if(in.equals("300000")) return "$300K";
		if(in.equals("400000")) return "$400K";
		if(in.equals("500000")) return "$500K";
		if(in.equals("600000")) return "$600K";
		if(in.equals("700000")) return "$700K";
		if(in.equals("800000")) return "$800K";
		if(in.equals("900000")) return "$900K";
		
		if(in.equals("1000000")) return "$1M";
		if(in.equals("1500000")) return "$1.5M";
		if(in.equals("2000000")) return "$2M";
		if(in.equals("2500000")) return "$2.5M";
		if(in.equals("3000000")) return "$3M";
		if(in.equals("3500000")) return "$3.5M";
		if(in.equals("4000000")) return "$4M";
		if(in.equals("4500000")) return "$4.5M";
		if(in.equals("5000000")) return "$5M";
		if(in.equals("10000000")) return "$10M";
		if(in.equals("20000000")) return "$20M";
		return "";
	}
}
