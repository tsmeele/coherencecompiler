package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class MinusIntegerOperand extends Term {

	@SuppressWarnings("rawtypes")
	public MinusIntegerOperand() {
		super();
		Class[] rule = {IntegerExpression.class};
		addRule(TokenType.MINUS, rule);
	}

}
