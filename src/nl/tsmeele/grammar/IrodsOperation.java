package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IrodsOperation extends Term {

	public IrodsOperation() {
		super();
		addRule(TokenType.IDENTIFIER, null);

	}

}
