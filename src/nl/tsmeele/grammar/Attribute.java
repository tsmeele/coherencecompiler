package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class Attribute extends Term {

	public Attribute() {
		super();
		addRule(TokenType.IDENTIFIER, null);
		
	}

}
