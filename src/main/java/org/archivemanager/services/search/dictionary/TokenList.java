package org.archivemanager.services.search.dictionary;
import java.util.ArrayList;

import org.archivemanager.services.search.Token;


public class TokenList extends ArrayList<Token> {
	private static final long serialVersionUID = 6752474422815940886L;


	public boolean isType(int index, int tokenType) {
		if(this.size() > index) {
			if(this.get(index).isType(tokenType)) return true;
		}
		return false;
	}
}
