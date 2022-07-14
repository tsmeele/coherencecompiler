package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class IntegerSubtraction extends Term {

	public IntegerSubtraction() {
		super();
		Class[] rule = {IntegerOperand.class};
		addRule(TokenType.MINUS, rule);
		addFunction(StackableFunctionType.INTEGER_SUBTRACTION);
	}
	
}
