package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class LiteralIntegerOperand extends Term {

	public LiteralIntegerOperand() {
		super();
		addRule(TokenType.INTEGERLITERAL, null);
	}

}
