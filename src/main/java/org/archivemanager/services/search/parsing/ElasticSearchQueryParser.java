package org.archivemanager.services.search.parsing;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.archivemanager.data.RequestBuffer;
import org.archivemanager.models.system.Group;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Dictionary;
import org.archivemanager.services.search.FilterRule;
import org.archivemanager.services.search.Parameter;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.Token;
import org.archivemanager.services.search.dictionary.CategoryDefinition;
import org.archivemanager.services.search.dictionary.CompoundDateDefinition;
import org.archivemanager.services.search.parsing.date.DateParsingUtility;
import org.archivemanager.util.NumberUtility;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;


@Component
public class ElasticSearchQueryParser implements QueryParser {
	@Autowired private QueryTokenizer tokenizer;
	@Autowired private Dictionary dictionary;
	protected List<ParserPlugin> plugins = new ArrayList<ParserPlugin>();
	
	private DateParsingUtility dateParser = new DateParsingUtility();
	
	
	public SearchRequest parse(SearchRequest request) {
		BoolQueryBuilder query = boolQuery();
		String queryString = null;
		RequestBuffer buff = new RequestBuffer();
		/*** Process Filter Rules ***/
		if(!request.getFilters().isEmpty()) {
			BoolQueryBuilder clauseQuery = boolQuery();
			for(FilterRule rule : request.getFilters()) {
				clauseQuery.must(termQuery(rule.getField(), rule.getValue()));
			}
			query.must(clauseQuery);
		}
		
		/*** Query String ***/
		if(request.getQuery() == null || request.getQuery().length() > 0 || !request.getQuery().equals("all results")) {
			List<Token> tokens = tokenizer.tokenize(request.getQuery());
			for(ParserPlugin plugin : plugins) {
				tokens = plugin.filter(tokens);
			}
			for(int i=0; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				if(token.getType() == Token.ALL) {
					if(tokens.size() == 1)
						query.must(matchAllQuery());
				}
				else if(token.getType() == Token.CATG) buff.append(getCategoryQuery(token));
				else if(token.isType(Token.ROOT)) {
					buff.append(token.getValue());
					if(token.getName().equals("(")) token.setInParens(true);
					else if(token.getName().equals(")")) token.setInParens(false);
				} else if(token.getType() == Token.ENTITY) {
					request.getQnames().add(new QName(token.getValue()));
				} else if(token.getType() == Token.DRNG && lookaheadType(tokens, i+1, Token.DRNG)) {
					String clause = dateParser.getDateClause(token, tokens.get(i));
					buff.append(clause);
					i++;
				} else {
					if(!token.inParens() && !buff.lastChar('-')) buff.append("+");
					if(token.isType(Token.MULT)) {
						buff.append("(");
						for(int j=0; j < token.getSubTypes().size(); j++) {
							token.setType(token.getSubTypes().get(j));
							token.setValue(token.getSubValues().get(j));
							tokenToQuery(token, request.getFields(), buff);
							buff.append(" OR ");
						}
						buff.setLength(buff.length()-4);
						buff.append(")");
					} else {
						if(token.getType() == Token.MULT) {
							buff.append("(");
							for(int k=0; k < token.getSubTypes().size(); k++) {
								Integer type = token.getSubTypes().get(k);
								if(type != Token.NUMB) {
									tokenToQuery(new Token(token.getName(), token.getSubValues().get(k), token.getSubTypes().get(k)), request.getFields(), buff);
									buff.append(" OR ");
								}
							}
							buff.setLength(buff.length()-4);
							buff.append(")");
						} else {
							tokenToQuery(token, request.getFields(), buff);
						}
					}
					buff.append(" ");
				}
			}					
		}
		if(request.getParameters().size() > 0) {
			for(Parameter p : request.getParameters()) {
				if(p.getValue() instanceof String)
					buff.append("+"+p.getName()+":\""+p.getValue()+"\" ");
				else
					buff.append("+"+p.getName()+":"+p.getValue()+" ");
			}
		}	
		queryString = buff.toString().trim();		
		request.setParse(queryString);
		if(queryString.length() > 0) {
			query.must(queryStringQuery(queryString));
		}		
		
		if(request.getClauses().size() > 0) {			
			for(Clause clause : request.getClauses()) {
				BoolQueryBuilder clauseQuery = boolQuery();
				for(Parameter parm : clause.getParameters()) {
					if(clause.getOperator().equals(Clause.OPERATOR_OR))
						clauseQuery.should(termQuery(parm.getName(), parm.getValue()));
					else if(clause.getOperator().equals(Clause.OPERATOR_AND))
						clauseQuery.must(termQuery(parm.getName(), parm.getValue()));
					else if(clause.getOperator().equals(Clause.OPERATOR_NOT))
						clauseQuery.mustNot(termQuery(parm.getName(), parm.getValue()));
				}
				query.must(clauseQuery);
			}		
		}
		if(request.getUser() != null) {
			BoolQueryBuilder aclQuery = boolQuery();
			if(request.isPublic()) {
				aclQuery.should(termQuery("isPublic", true));
				aclQuery.should(termQuery("acl", "0"));
			}
			if(!request.getUser().isGuest()) {
				aclQuery.should(termQuery("acl", String.valueOf(request.getUser().getId())));
				for(Group group : request.getUser().getGroups()) {
					aclQuery.should(termQuery("acl", String.valueOf(group.getId())));
				}
			}
			query.must(aclQuery);
		}
		/***QName ***/
		String[] qnames = new String[0];
		if(request.getRelationships().size() > 0) {
			qnames = new String[request.getRelationships().size()];
			int idx = 0;
			for(QName qname : request.getRelationships()) {
				if(qname != null) {
					qnames[idx] = qname.toString();
					idx++;
				}
			}
		} else {
			qnames = new String[request.getQnames().size()];
			int idx = 0;
			for(QName qname : request.getQnames()) {
				if(qname != null) {
					qnames[idx] = qname.toString();
					idx++;
				}
			}
		}
		if(qnames.length > 0) {
			TermsQueryBuilder qnameQuery = termsQuery("qname", qnames);
			query.must(qnameQuery);
		}
		request.setNativeQuery(query);
		return request;
	}
	
	protected String getCategoryQuery(Token token) {
		StringBuffer buff = new StringBuffer();
		CategoryDefinition def = (CategoryDefinition)dictionary.lookup(Token.CATG, token.getName());
		try {
			if(def.getIds().size() == 1) {
				return "openapps_org_system_1_0_path:"+def.getIds().get(0);
			} else if(def.getIds().size() > 0) {
				for(String category : def.getIds()) {
					buff.append(category+" OR ");
				}
				return "openapps_org_system_1_0_path:("+buff.substring(0, buff.length()-4).toString()+")";
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public String getDateQuery(Token token) {
		return token.getName()+":"+token.getValue();
	}
	public String getYearQuery(Token token) {
		if(token.getDefinitions().size() > 0 && token.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			StringWriter writer = new StringWriter();
			CompoundDateDefinition def = (CompoundDateDefinition)token.getDefinitions().get(0);
			String[] chunks = def.getValue().split(" ");
			String year = "";
			for(String chunk : chunks) {
				if(NumberUtility.isInteger(chunk)) year = chunk;
			}
			writer.append("(");
			for(Clause clause : def.getClauses()) {
				writer.append("(");
				for(Parameter parm : clause.getParameters()) {
					if(parm.getName().equals("date")) writer.append("+"+parm.getValue()+":["+year+"0101 TO "+year+"1231] ");
					else writer.append("+"+parm.getName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
			writer.append(") ");
			return writer.toString().trim();
		} else {
			String[] value = token.getValue().split(":");
			if(value.length == 2) {
				return value[0]+":["+value[1]+"0101 TO "+value[1]+"1231]";
			}
			return token.getValue();
		}
	}
	public String getMonthQuery(Token token) {		
		if(token.getDefinitions().size() > 0 && token.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			StringWriter writer = new StringWriter();
			CompoundDateDefinition def = (CompoundDateDefinition)token.getDefinitions().get(0);
			String[] value = def.getValue().split(" ");
			writer.append("(");
			for(Clause clause : def.getClauses()) {
				String month = "";	
				if(value[0].equals("december")) month = "12";
				if(value[0].equals("november")) month = "11";
				if(value[0].equals("october")) month = "10";	
				if(value[0].equals("september")) month = "09";
				if(value[0].equals("august")) month = "08";
				if(value[0].equals("july")) month = "07";
				if(value[0].equals("june")) month = "06";
				if(value[0].equals("may")) month = "05";
				if(value[0].equals("april")) month = "04";
				if(value[0].equals("march")) month = "03";
				if(value[0].equals("february")) month = "02";
				if(value[0].equals("january")) month = "01";					
				writer.append("(");
				for(Parameter parm : clause.getParameters()) {
					if(parm.getName().equals("date")) writer.append("+"+parm.getValue()+":["+value[1]+month+"01 TO "+value[1]+month+"31] ");
					else writer.append("+"+parm.getName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
			writer.append(") ");
			return writer.toString().trim();
		} else {
			Calendar current = Calendar.getInstance();
			String[] value = token.getValue().split(":");
			String month = "";	
			if(value.length == 2) {
				if(value[1].equals("december")) month = "12";
				if(value[1].equals("november")) month = "11";
				if(value[1].equals("october")) month = "10";	
				if(value[1].equals("september")) month = "09";
				if(value[1].equals("august")) month = "08";
				if(value[1].equals("july")) month = "07";
				if(value[1].equals("june")) month = "06";
				if(value[1].equals("may")) month = "05";
				if(value[1].equals("april")) month = "04";
				if(value[1].equals("march")) month = "03";
				if(value[1].equals("february")) month = "02";
				if(value[1].equals("january")) month = "01";					
			}
			return value[0]+":["+current.get(Calendar.YEAR)+month+"01 TO "+current.get(Calendar.YEAR)+month+"31]";
		}
	}
	protected boolean lookaheadType(List<Token> tokens, int index, int tokenType) {
		if(tokens.size() > index) {
			Token token = tokens.get(index);
			if(token != null && token.getType() == tokenType) return true;
		}
		return false;
	}
	protected void tokenToQuery(Token token, String[] fields, RequestBuffer buff) {
		if(token.getType() == Token.TEXT) {
			if(fields.length > 1) {
				buff.append("(");
				for(int i=0; i < fields.length; i++) {
					String field = fields[i];
					buff.append(field+":"+token.getValue());
					if(i < fields.length-1) buff.append(" OR ");
				}
				buff.append(")");
			} else if(fields.length == 1) buff.append(fields[0]+":"+token.getValue());
			else buff.append("openapps_org_system_1_0_name:"+token.getValue());
		}
		else if(token.getType() == Token.PROP || token.getType() == Token.NUMB) buff.append(token.getValue());
		else if(token.getType() == Token.TERM || token.getType() == Token.QNAM) {
			if(token.getSubTokens().size() > 0) {
				if(!token.inParens()) buff.append("(");
				RequestBuffer buff2 = new RequestBuffer();
				for(Token t : token.getSubTokens()) {
					buff2.append("+");
					tokenToQuery(t, fields, buff2);
					buff2.append(" ");
				}
				if(!token.inParens()) buff.append(buff2.toString().trim()+")");
			} else {
				if(token.inParens()) buff.append("("+token.getValue()+")");
				else buff.append(token.getValue());
			}
		} else if(token.getType() == Token.ATTR || token.getType() == Token.ID || token.getType() == Token.PATH || token.getType() == Token.COLM) {
			buff.append(token.getName()+":"+token.getValue());
		} else if(token.getType() == Token.NAME) {
			if(NumberUtility.isLong(token.getValue()))
				//buff.append("(source_assoc"+":"+token.getValue()+" OR source_assoc_inherited"+":"+token.getValue()+")");
				buff.append("source_assoc"+":"+token.getValue());
			else
				buff.append("name:"+token.getValue());
		} else if(token.getType() == Token.SUBJ) {
			//buff.append("(source_assoc"+":"+token.getValue()+" OR source_assoc_inherited"+":"+token.getValue()+")");
			buff.append("source_assoc"+":"+token.getValue());
		} else if(token.getType() == Token.DATE) {
			buff.append(getDateQuery(token));
		} else if(token.getType() == Token.YEAR) {
			buff.append(getYearQuery(token));
		} else if(token.getType() == Token.MONTH) {
			buff.append(getMonthQuery(token));
		} else if(token.getType() == Token.DRNG) {
			String clause = dateParser.getDateClause(token);
			buff.append(clause);
		} else if(token.getType() == Token.CATG) {
			buff.append(getCategoryQuery(token));
		} 
	}
}
