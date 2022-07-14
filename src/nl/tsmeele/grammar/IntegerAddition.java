package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class IntegerAddition extends Term {

	@SuppressWarnings("rawtypes")
	public IntegerAddition() {
		super();
		Class[] rule = {IntegerOperand.class};
		addRule(TokenType.PLUS, rule);
		addFunction(StackableFunctionType.INTEGER_ADDITION);
	}
	

}
