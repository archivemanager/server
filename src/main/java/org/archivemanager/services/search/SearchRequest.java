package org.archivemanager.services.search;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.data.Sort;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Parameter;
import org.archivemanager.services.search.aggregation.SearchAggregation;


public class SearchRequest {
	private long id;
	private long xid;	
	private String index = "nodes"; 
	private String context = "";
	private Long nodeId;
	private String query;		
	private String operator = "OR";	
	private User user;
	private String parse;
	private int startRow = 0;
	private int endRow = 0;
	private boolean deleted = false;
	private boolean sources = false;
	private boolean tokenized = true;
	private boolean caseSensitive = false;
	private boolean isPublic = true;
	private List<QName> qnames = new ArrayList<QName>();
	private List<QName> relationships = new ArrayList<QName>();
	private Map<String, String[]> requestParameters = new HashMap<String, String[]>();
	private String[] fields = new String[0];
	
	private List<SearchAggregation> aggregations = new ArrayList<SearchAggregation>();
	private List<FilterRule> filters = new ArrayList<FilterRule>();
	private List<Parameter> parameters = new ArrayList<Parameter>();
	private List<Clause> clauses = new ArrayList<Clause>();
	private List<Sort> sorts = new ArrayList<Sort>();
	private List<Long> groups = new ArrayList<Long>();
	
	
	private Object nativeQuery;
	
	public static final String OPERATOR_OR = "OR";
	public static final String OPERATOR_AND = "AND";
	
	public SearchRequest() {}
	public SearchRequest(QName qname) {
		this.qnames.add(qname);
	}
	public SearchRequest(QName qname, String query) {
		this.qnames.add(qname);
		this.query = query;
	}
	public SearchRequest(QName qname, QName relationship, String query) {
		this.qnames.add(qname);
		if(relationship != null) this.relationships.add(relationship);
		this.query = query;
	}
	public SearchRequest(String query, QName... qnames) {
		for(QName q : qnames) {
			this.qnames.add(q);
		}
		this.query = query;
	}
	public SearchRequest(QName entityQname, String query, String sort, String direction) {
		this.qnames.add(entityQname);
		this.query = query;
		addSort(new Sort(ModelField.TYPE_SMALLTEXT, sort, direction));
	}
	public SearchRequest(QName entityQname, String field, String queryString, String sort, String direction) {
		this.qnames.add(entityQname);
		this.query = queryString;
		this.fields = new String[] {field};
		addSort(new Sort(ModelField.TYPE_SMALLTEXT, sort, direction));
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getXid() {
		return xid;
	}
	public void setXid(long xid) {
		this.xid = xid;
	}
	/*** which index to search nodes, associations, events etc. ***/
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public QName getQName() {
		if(qnames.size() > 0) return qnames.get(0);
		return null;
	}
	public void setQName(QName qname) {
		this.qnames.add(qname);
	}
	public List<QName> getQnames() {
		return qnames;
	}
	public void setQNames(List<QName> qnames) {
		this.qnames = qnames;
	}
	public void setQNames(QName[] qnames) {
		for(QName qname : qnames) {
			if(!this.qnames.contains(qname))
				this.qnames.add(qname);
		}
	}
	public List<QName> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<QName> relationships) {
		this.relationships = relationships;
	}
	public void setRelationships(QName[] relationships) {
		for(QName relationship : relationships) {
			if(!this.relationships.contains(relationship))
				this.relationships.add(relationship);
		}
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public List<FilterRule> getFilters() {
		return filters;
	}
	public void setFilters(List<FilterRule> filters) {
		this.filters = filters;
	}
	public void addParameter(Parameter p) {
		parameters.add(p);
	}
	public void addParameter(String type, String name, Object value) {
		parameters.add(new Parameter(type, name, value));
	}
	public void addParameter(String name, Object value) {
		parameters.add(new Parameter(name, value));
	}
	public void addParameter(QName qname, Object value) {
		parameters.add(new Parameter(qname.toString(), value));
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	public void addClause(Clause clause) {
		clauses.add(clause);
	}
	public List<Clause> getClauses() {
		return clauses;
	}
	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}
	public List<Sort> getSorts() {
		return sorts;
	}
	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}
	public void addSort(Sort sort) {
		sorts.add(sort);
	}	
	public List<Long> getGroups() {
		return groups;
	}
	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}	
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public boolean printSources() {
		return sources;
	}
	public void setPrintSources(boolean sources) {
		this.sources = sources;
	}	
	public boolean isTokenized() {
		return tokenized;
	}
	public void setTokenized(boolean tokenized) {
		this.tokenized = tokenized;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Map<String, String[]> getRequestParameters() {
		return requestParameters;
	}
	public void setRequestParameters(Map<String, String[]> requestParameters) {
		this.requestParameters = requestParameters;
	}	
	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}	
	public Object getNativeQuery() {
		return nativeQuery;
	}
	public void setNativeQuery(Object nativeQuery) {
		this.nativeQuery = nativeQuery;
	}		
	public String getParse() {
		return parse;
	}
	public void setParse(String parse) {
		this.parse = parse;
	}
	public List<SearchAggregation> getAggregations() {
		return aggregations;
	}
	public void setAggregations(List<SearchAggregation> aggregations) {
		this.aggregations = aggregations;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + ((clauses == null) ? 0 : clauses.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(fields);
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((qnames == null) ? 0 : qnames.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((relationships == null) ? 0 : relationships.hashCode());
		result = prime * result + ((requestParameters == null) ? 0 : requestParameters.hashCode());
		result = prime * result + ((sorts == null) ? 0 : sorts.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchRequest other = (SearchRequest) obj;
		if (caseSensitive != other.caseSensitive)
			return false;
		if (clauses == null) {
			if (other.clauses != null)
				return false;
		} else if (!clauses.equals(other.clauses))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (deleted != other.deleted)
			return false;
		if (!Arrays.equals(fields, other.fields))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (qnames == null) {
			if (other.qnames != null)
				return false;
		} else if (!qnames.equals(other.qnames))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (relationships == null) {
			if (other.relationships != null)
				return false;
		} else if (!relationships.equals(other.relationships))
			return false;
		if (requestParameters == null) {
			if (other.requestParameters != null)
				return false;
		} else if (!requestParameters.equals(other.requestParameters))
			return false;
		if (sorts == null) {
			if (other.sorts != null)
				return false;
		} else if (!sorts.equals(other.sorts))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
}
