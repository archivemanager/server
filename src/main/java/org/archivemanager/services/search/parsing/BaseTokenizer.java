package org.archivemanager.services.search.parsing;
import java.util.List;

import javax.annotation.PostConstruct;

import org.archivemanager.services.search.Definition;
import org.archivemanager.services.search.Dictionary;
import org.archivemanager.services.search.Token;
import org.archivemanager.services.search.TokenTypes;
import org.archivemanager.services.search.dictionary.AttributeDefinition;
import org.archivemanager.services.search.dictionary.NumericDefinition;
import org.archivemanager.services.search.dictionary.RootDefinition;
import org.archivemanager.services.search.dictionary.TokenList;
import org.archivemanager.util.NumberUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseTokenizer implements QueryTokenizer {
	@Autowired private Dictionary dictionary;
	private LexNode root = new LexNode();
		
	private int state = 0;
	private int nodeCount = 0;
	
	@PostConstruct
	public void initialize() {
		List<Definition> definitions = dictionary.getDefinitions();
		loadRoots();
		for(Definition def : definitions) {
        	try {        		
        		loadDefinition(def);
        	} catch(Exception e) {
                e.printStackTrace();
            }
        }
        this.state = 1;
	}
	@SuppressWarnings("unused")
	@Override
	public List<Token> tokenize(String query) {
		TokenList tokens = new TokenList();
		if(query == null || query.length() == 0) return tokens;
		StringBuffer text = new StringBuffer();
		LexNode node = root;
		LexNode hit = null;
		boolean inBracket = false;
		boolean inParens = false;
		//scan for whitespace or delimiters
		for(int i=0; i < query.length(); i++) {
			switch(query.charAt(i)) {
			case ' ':
				if(node != null) {
					if(node.hasDefinition()) 
						hit = node;
					node = node.getChild(query.charAt(i));
				}
				if(!inBracket && node == null) {
					if(hit != null) {
						generateTokens(tokens, text, hit.getDefinitions());
						int defLength = hit.getDefinedValue().length();
						int newLength = i - text.length() + defLength;
						i = newLength;
						text = new StringBuffer();
						node = root;
						hit = null;
					} else {
						generateToken(tokens, text);
						text = new StringBuffer();
						node = root;
					}
				} else {					
					text.append(query.charAt(i));
				}
				break;
			case '/':
				if(i > 1 && query.charAt(i-1) == '/') {
					if(hit != null) {
						generateTokens(tokens, text, hit.getDefinitions());
						int defLength = hit.getDefinedValue().length();
						int newLength = i - text.length() + defLength;
						i = newLength;
						text = new StringBuffer();
						node = root;
						hit = null;
					} else {
						generateToken(tokens, text);
						text = new StringBuffer();
						node = root;
					}
				}
				break;
			case '(':
				inParens = true;
				generateToken(tokens, text);
				text = new StringBuffer();
				tokens.add(new Token("(", "+(", Token.ROOT));
				break;
			case ')':
				inParens = false;
				generateToken(tokens, text);
				text = new StringBuffer();
				tokens.add(new Token(")", ")", Token.ROOT));
				break;
			case '[':
				inBracket = true;
				text.append(query.charAt(i));
				break;
			case ']':
				inBracket = false;
				text.append(query.charAt(i));
				break;
			default:
				if(node != null) node = node.getChild(query.charAt(i));
				if(node != null && node.hasDefinition() && isSeparator(query, i+1))
					hit = node;
				if(node == null && hit != null) {
					generateTokens(tokens, text, hit.getDefinitions());
					int defLength = hit.getDefinedValue().length();
					int newLength = i - text.length() + defLength;
					i = newLength;
					text = new StringBuffer();
					node = root;
					hit = null;
				} else {
					text.append(query.charAt(i));
				}
			}
		}
		if(text.length() > 0) generateToken(tokens, text);
		for(Token token : tokens) {
			if(token.isType(Token.TERM)) {
				List<Token> subtokens = tokenize(token.getValue());
				if(subtokens.size() > 1) {
					token.setSubTokens(subtokens);
					for(Token subtoken : subtokens) {
						if(subtoken.isType(Token.TERM)) {
							List<Token> subsubtokens = tokenize(subtoken.getValue());
							if(subsubtokens.size() > 1) {
								subtoken.setSubTokens(subsubtokens);
							}
						}
					}
				}
			}
		}
		merge(tokens);
		return tokens;
	}
	public int getState() {
		return state;
	}
	public int getNodeCount() {
		return nodeCount;
	}
	
	protected void merge(TokenList tokens) {
		boolean merged = false;
		while(!merged) {
			merged = true;
			for(int i=0; i < tokens.size(); i++) {
				if(i <tokens.size()-1) {
					Token token1 = tokens.get(i);
					Token token2 = tokens.get(i + 1);
					if(token1.isType(TokenTypes.TEXT) && token2.isType(TokenTypes.TEXT)) {
						String value = token1.getName()+" "+token2.getName();
						token1.setName(value);
						token1.setValue(value);
						tokens.remove(i + 1);
						merged = false;
					}
				}
			}
		}
	}
	protected void generateToken(TokenList tokens, StringBuffer text) {
		if(text.toString().length() > 0) {
			boolean matched = false;		
			if(text.toString().indexOf(":") > -1) {
				String[] parts = text.toString().toString().split(":");
				if(parts[1].startsWith("[") && parts[1].endsWith("]")) 
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.DATE));
				else if(parts[0].equals("source_assoc"))
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.ATTR));
				else if(parts[0].equals("name"))
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.NAME));
				else if(parts[0].equals("subj"))
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.SUBJ));
				else if(parts[0].equals("id") || parts[0].equals("parent_id"))
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.ID));
				else if(parts[0].equals("path"))
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.PATH));
				else 
					//tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.PROP));
					tokens.add(new Token(parts[0].trim(), parts[1].trim(), Token.COLM));
				matched = true;
			} else {
				LexNode node = root;
				for(int i=0; i < text.length(); i++) {
					if(node != null) node = node.getChild(text.charAt(i));
					if(node == null) break;
				}
				if(node != null && node.hasDefinition()) {
					generateTokens(tokens, text, node.getDefinitions());
					matched = true;
				}
			}
			if(!matched) {
				if(NumberUtility.isInteger(text.toString())) tokens.add(new Token(text.toString(), text.toString(), Token.NUMB));
				else tokens.add(new Token("", text.toString(), Token.TEXT));
			}
		}
	}
	protected void generateTokens(TokenList tokens, StringBuffer text, List<Definition> defs) {
		if(defs.size() > 1) {
			Token token = new Token(defs, Token.MULT);
			for(Definition def : defs) {
				token.getSubTypes().add(def.getType());
				token.getSubValues().add(def.getValue());
			}
			tokens.add(token);
		} else {
			if(defs.get(0) instanceof AttributeDefinition) {
				String prefix = defs.get(0).getName().substring(0, defs.get(0).getName().indexOf(":"));
				String[] parts = text.toString().split(":");
				CharSequence seq = text.subSequence(0, text.indexOf(prefix));
				if(seq.length() == 0 || seq.equals(" ")){
					tokens.add(new Token(prefix, parts[1], Token.ATTR));
				} else {
					text = new StringBuffer(seq.toString().trim());
					tokens.add(new Token(text.toString(), text.toString(), Token.TEXT));
					tokens.add(new Token(prefix, parts[1], Token.ATTR));
				}
				text = new StringBuffer();
			} else
				tokens.add(new Token(defs.get(0)));
		}
	}
	protected boolean isSeparator(String query, int index) {
		if(query.length() <= index) return true;
		if(query.charAt(index) == ' ' || query.charAt(index) == '/') return true;
		return false;
	}
	protected void loadRoots() {
		try {
			loadDefinition(new RootDefinition("not", "-"));
		} catch(Exception e) {
            e.printStackTrace();
        }
	}
	protected void loadDefinition(Definition def) throws Exception {
		LexNode node = root;
		for(int i=0; i < def.getName().length(); i++) {
			char ch = def.getName().charAt(i);
			LexNode n = node.getChild(ch);
			if(n != null) node = n;
			else {
				nodeCount++;
				if(def.getName().charAt(i) == '*') {
					if(def instanceof AttributeDefinition) {
						if(((AttributeDefinition)def).isNumeric())
							n = new NumericLexNode(node);
					}
				} else n = new LexNode(def.getName().charAt(i), node, def.isCaseSensitive());
				if(n != null) node = n;
			}
			if(i == def.getName().length()-1) {
				n.addDefinition(def);
				if(NumberUtility.isInteger(def.getName()) && def.getType() != Token.YEAR)
					n.addDefinition(new NumericDefinition(def.getName()));
			}
		}		
	}
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
}
