package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IntegerSubtraction extends Term {

	@SuppressWarnings("rawtypes")
	public IntegerSubtraction() {
		super();
		Class[] rule = {IntegerOperand.class};
		addRule(TokenType.MINUS, rule);
		addFunction(StackableFunctionType.INTEGER_SUBTRACTION);
	}
	
}
