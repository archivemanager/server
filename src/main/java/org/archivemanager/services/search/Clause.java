package org.archivemanager.services.search;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Property;


public class Clause {
	public String operator;
	public List<Property> properties = new ArrayList<Property>();
	public List<Parameter> parameters = new ArrayList<Parameter>();
	
	public static final String OPERATOR_OR = "OR";
	public static final String OPERATOR_AND = "AND";
	public static final String OPERATOR_NOT = "NOT";
	
	
	public Clause() {}
	public Clause(String operator, Property p1) {
		this.operator = operator;
		properties.add(p1);
	}
	public Clause(String operator, Property p1, Property p2) {
		this.operator = operator;
		properties.add(p1);
		properties.add(p2);
	}
	public Clause(String operator, Property p1, Property p2, Property p3) {
		this.operator = operator;
		properties.add(p1);
		properties.add(p2);
		properties.add(p3);
	}
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public void addProperty(QName name, String value) {
		properties.add(new Property(name, value));
	}
	public void addProperty(Property parm) {
		properties.add(parm);
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}	
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	
}
