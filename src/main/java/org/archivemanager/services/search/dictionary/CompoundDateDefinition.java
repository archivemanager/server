package org.archivemanager.services.search.dictionary;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Parameter;


public class CompoundDateDefinition extends DefinitionSupport {
	private List<Clause> clauses = new ArrayList<Clause>();
	private List<Parameter> parameters = new ArrayList<Parameter>();
	private String operator;
	
	
	public CompoundDateDefinition(int type, int value, String operator) {
		super(type, String.valueOf(value), String.valueOf(value));
		this.operator = operator;
	}
	public CompoundDateDefinition(int type, String value, String operator) {
		super(type, String.valueOf(value), value);
		this.operator = operator;
	}
	
	public List<Clause> getClauses() {
		return clauses;
	}
	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
