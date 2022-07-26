package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IntegerOperandLiteral extends Term {

	public IntegerOperandLiteral() {
		super();
		addRule(TokenType.INTEGERLITERAL, null);
	}

}
