package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IrodsAttribute extends Term {

	public IrodsAttribute() {
		super();
		addRule(TokenType.IDENTIFIER, null);
		
	}

}
