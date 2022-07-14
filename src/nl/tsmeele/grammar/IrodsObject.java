package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IrodsObject extends Term {

	public IrodsObject() {
		super();
		addRule(TokenType.IDENTIFIER, null);
		
	}

}
