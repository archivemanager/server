package org.archivemanager.services.dictionary;

import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.services.data.ExportProcessorSupport;
import org.springframework.stereotype.Component;


@Component
public class XmlDictionaryExporter extends ExportProcessorSupport {

	
	public String export(DataDictionary dictionary) {
		StringBuffer buff = new StringBuffer("<?xml version=\"1.0\"?>\n");
		buff.append("<dictionary qname=\""+dictionary.getQName()+"\" name=\""+dictionary.getName()+"\" label=\""+clean(dictionary.getLabel())+"\" description=\""+dictionary.getDescription()+"\">\n");
		buff.append("	<models>\n");
		for(Model model : dictionary.getModels()) {
		buff.append("		<model namespace=\""+model.getQName().getNamespace()+"\" localname=\""+model.getQName().getLocalName()+"\" label=\""+clean(model.getLabel())+"\" stored=\""+model.isStored()+"\" searchable=\""+model.isSearchable()+"\">\n");
		if(model.getParent() != null)
		buff.append("			<parent namespace=\""+model.getParentName().getNamespace()+"\" localname=\""+model.getParentName().getLocalName()+"\"/>\n");
		if(model.getLabelName() != null)
		buff.append("			<labelname namespace=\""+model.getLabelName().getNamespace()+"\" localname=\""+model.getLabelName().getLocalName()+"\"/>\n");
		buff.append("			<description>"+model.getDescription()+"</description>\n");
		buff.append("			<properties>\n");
		for(ModelField field : model.getFields()) {
		buff.append("				<property type=\""+field.getType()+"\" namespace=\""+field.getQName().getNamespace()+"\" localname=\""+field.getQName().getLocalName()+"\" mandatory=\""+field.isMandatory()+"\" unique=\""+field.isUnique()+"\" hidden=\""+field.isHidden()+"\" label=\""+clean(field.getLabel())+"\" searchable=\""+field.isSearchable()+"\">\n");
									if(field.getValues().size() > 0) {
		buff.append("					<values>\n");
										for(ModelFieldValue value : field.getValues()) {
		buff.append("						<value value=\""+value.getValue()+"\" label=\""+value.getLabel()+"\" />\n");
										}
		buff.append("					</values>\n");
									}
		buff.append("				</property>\n");
		}
		buff.append("			</properties>\n");
		buff.append("			<associations>\n");
		for(ModelRelation relation : model.getRelations()) {
		buff.append("				<association namespace=\""+relation.getQName().getNamespace()+"\" localname=\""+relation.getQName().getLocalName()+"\" label=\""+clean(relation.getLabel())+"\" hidden=\""+relation.isHidden()+"\" cascade=\""+relation.isCascade()+"\" many=\""+relation.isMany()+"\" view=\""+clean(relation.getView())+"\">\n");
		buff.append("					<target namespace=\""+relation.getEndName().getNamespace()+"\" localname=\""+relation.getEndName().getLocalName()+"\"></target>\n");
		buff.append("				</association>\n");
		}
		buff.append("			</associations>\n");
		buff.append("		</model>\n");
		}			
		buff.append("	</models>\n");
		buff.append("</dictionary>");
		return buff.toString();
	}
}
