package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IntegerAddition extends Term {

	@SuppressWarnings("rawtypes")
	public IntegerAddition() {
		super();
		Class[] rule = {IntegerOperand.class};
		addRule(TokenType.PLUS, rule);
		addFunction(StackableFunctionType.INTEGER_ADDITION);
	}
	

}
