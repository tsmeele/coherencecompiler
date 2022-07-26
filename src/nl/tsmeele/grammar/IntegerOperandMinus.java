package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IntegerOperandMinus extends Term {

	@SuppressWarnings("rawtypes")
	public IntegerOperandMinus() {
		super();
		Class[] rule = {IntegerExpression.class};
		addRule(TokenType.MINUS, rule);
	}

}
