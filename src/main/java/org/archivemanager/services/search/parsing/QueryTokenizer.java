package org.archivemanager.services.search.parsing;
import java.util.List;

import org.archivemanager.services.search.Token;


public interface QueryTokenizer {

	void initialize();
	List<Token> tokenize(String query);
	
}
