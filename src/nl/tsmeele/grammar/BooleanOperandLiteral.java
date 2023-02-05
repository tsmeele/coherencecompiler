package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class BooleanOperandLiteral extends Term {

	public BooleanOperandLiteral() {
		super();
		addRule(TokenType.BOOLEANLITERAL, null);
	}
}
