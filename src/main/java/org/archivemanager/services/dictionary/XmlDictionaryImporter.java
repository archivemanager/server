package org.archivemanager.services.dictionary;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.util.StringUtility;
import org.archivemanager.util.XMLUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlDictionaryImporter {
	private DataDictionary dictionary;
	
	
	public void process(InputStream stream) throws Exception {
		XMLUtility.SAXParse(false, stream, new ImportHandler());
	}
	public DataDictionary getDictionary() {
		return dictionary;
	}
		
	public class ImportHandler extends DefaultHandler {
		StringBuffer buff = new StringBuffer();
		Map<String,String> namespaces = new HashMap<String,String>();
		Model model;
		ModelRelation relation;
		ModelField field;		
		ModelFieldValue value;
		
		public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
			if(qName.equals("dictionary")) {
				dictionary = new DataDictionary();
				String nameAttr =  attrs.getValue("name");
				String labelAttr =  attrs.getValue("label");
				String qnameAttr =  attrs.getValue("qname");
				String descriptionAttr =  attrs.getValue("description");
				if(nameAttr != null) dictionary.setName(nameAttr);
				if(labelAttr != null) dictionary.setLabel(labelAttr);
				if(qnameAttr != null) dictionary.setQName(new QName(qnameAttr));
				if(descriptionAttr != null) dictionary.setDescription(descriptionAttr);
			} else if(qName.equals("model")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				String label = attrs.getValue("label");
				String searchable = attrs.getValue("searchable");
				String stored = attrs.getValue("stored");
				String header = attrs.getValue("header");
				if(namespaceAttr != null && localnameAttr != null) {
					model = new Model(null, new QName(namespaceAttr, localnameAttr));
					model.setUid(java.util.UUID.randomUUID().toString());
					if(header != null) model.setHeader(header);
					if(searchable != null) model.setSearchable(Boolean.valueOf(searchable));
					else model.setSearchable(false);
					if(stored != null) model.setStored(Boolean.valueOf(stored));
					else model.setStored(true);
					if(label != null) model.setLabel(label);
				}
			} else if(qName.equals("property")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				if(namespaceAttr != null && localnameAttr != null) {
					field = new ModelField(0, new QName(namespaceAttr, localnameAttr));
					field.setUid(java.util.UUID.randomUUID().toString());
					String type = attrs.getValue("type");
					if(type != null && type.length() > 0) {
						field.setType(type);
					}
					String format = attrs.getValue("format");
					if(format != null && format.length() > 0) {
						field.setFormat(format);
					}
					String mandatory = attrs.getValue("mandatory");
					if(mandatory != null && mandatory.length() > 0) {
						field.setMandatory(Boolean.valueOf(type));
					}
					String unique = attrs.getValue("unique");
					if(unique != null && unique.length() > 0) {
						field.setUnique(Boolean.valueOf(unique));
					}
					String hidden = attrs.getValue("hidden");
					if(hidden != null && hidden.length() > 0) {
						field.setHidden(Boolean.valueOf(hidden));
					}
					String label = attrs.getValue("label");
					if(label != null && label.length() > 0) {
						field.setLabel(label);
					} else field.setLabel(StringUtility.toTitleCase(localnameAttr));
					
					String searchable = attrs.getValue("searchable");
					if(searchable != null && searchable.equals("true")) 
						field.setSearchable(true);
					else field.setSearchable(false);					
					
					String sortable = attrs.getValue("sortable");
					if(sortable != null && sortable.equals("true")) field.setSortable(true);
					else field.setSortable(false);
				}
			} else if(qName.equals("value")) {
				value = new ModelFieldValue();
				String labelAttr =  attrs.getValue("label");
				if(labelAttr != null && labelAttr.length() > 0) {
					value.setLabel(labelAttr);
				}
				String valueAttr =  attrs.getValue("value");
				if(valueAttr != null && valueAttr.length() > 0) {
					value.setValue(valueAttr);
				}
			} else if(qName.equals("association")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				if(namespaceAttr != null && localnameAttr != null) {
					relation = new ModelRelation(null, model.getQName(), null, ModelRelation.DIRECTION_OUTGOING, new QName(namespaceAttr, localnameAttr));
					relation.setUid(java.util.UUID.randomUUID().toString());
					
					String type = attrs.getValue("type");
					if(type != null && type.length() > 0) {
						relation.setType(type);
					}
					
					String view = attrs.getValue("view");
					if(view != null && view.length() > 0) {
						relation.setView(view);
					}
					
					String mapping = attrs.getValue("mapping");
					if(mapping != null && mapping.length() > 0) {
						relation.setMapping(new QName(mapping));
					}
					
					String label = attrs.getValue("label");
					if(label != null && label.length() > 0) {
						relation.setLabel(label);
					} else relation.setLabel(StringUtility.toTitleCase(localnameAttr));
					
					String many = attrs.getValue("many");
					if(many == null) many = "false";
					relation.setMany(Boolean.parseBoolean(many));
					
					String cascade = attrs.getValue("cascade");
					if(cascade == null) cascade = "false";
					relation.setCascade(Boolean.parseBoolean(cascade));
					
					String hidden = attrs.getValue("hidden");
					if(hidden == null) hidden = "false";
					relation.setHidden(Boolean.parseBoolean(hidden));
				}
			} else if(qName.equals("target")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				if(relation != null && namespaceAttr != null && localnameAttr != null) {
					relation.setEndName(new QName(namespaceAttr, localnameAttr));
				}
			} else if(qName.equals("parent")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				if(model != null && namespaceAttr != null && localnameAttr != null) {
					model.setParentName(new QName(namespaceAttr, localnameAttr));
				}
			} else if(qName.equals("labelname")) {
				String namespaceAttr =  attrs.getValue("namespace");
				String localnameAttr =  attrs.getValue("localname");
				if(model != null && namespaceAttr != null && localnameAttr != null) {
					model.setLabelName(new QName(namespaceAttr, localnameAttr));
				}
			}
		}
	
		public void characters(char[] ch, int start, int length) throws SAXException {
			buff.append(ch,start,length);
		}
	
		public void endElement(String namespaceURI, String sName, String qName) throws SAXException	{
			String data = buff.toString().trim();
			if(qName.equals("value")) {
				if(data != null && data.length() > 0) {
					value.setLabel(data);
					if(value.getValue() == null)
						value.setValue(data);
				}
				if(value.getValue() != null && value.getValue().length() > 0) 
					field.getValues().add(value);				
			} else if(qName.equals("type") && data != null && field != null) {
				field.setType(data);
			} else if(qName.equals("property")) {
				if(relation != null) {
					relation.getFields().add(field);
				} else if(model != null) {
					model.getFields().add(field);
				}			
			} else if(qName.equals("association") && relation != null) {
				model.getSourceRelations().add(relation);
				relation = null;
			} else if(qName.equals("label")) {
				if(model != null) model.setLabel(data);
				else if(dictionary != null) dictionary.setName(data);
			} else if(qName.equals("description")) {
				if(model != null) model.setDescription(data);
				else if(dictionary != null) dictionary.setDescription(data);
			} else if(qName.equals("model")) {
				if(model != null) dictionary.getModels().add(model);
			} 
			buff = new StringBuffer();
		}		
	}
}
