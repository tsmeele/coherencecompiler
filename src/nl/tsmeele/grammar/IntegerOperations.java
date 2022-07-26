package nl.tsmeele.grammar;

import nl.tsmeele.compiler.NonConsumingOptionalTerm;

public class IntegerOperations extends NonConsumingOptionalTerm {

	@SuppressWarnings("rawtypes")
	public IntegerOperations() {
		super();
		Class[] additionRule = {IntegerAddition.class, IntegerOperations.class};
		addRule(TokenType.PLUS, additionRule);
		Class[] subtractionRule = {IntegerSubtraction.class, IntegerOperations.class};
		addRule(TokenType.MINUS, subtractionRule);
	}

}
