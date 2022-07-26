package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class RoleName extends Term {

	public RoleName() {
		super();
		addRule(TokenType.IDENTIFIER, null);

	}

}
