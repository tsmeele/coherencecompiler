package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class IntegerOperation extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public IntegerOperation() {
		super();
		Class[] additionRule = {IntegerAddition.class, IntegerOperation.class};
		addRule(TokenType.PLUS, additionRule);
		Class[] subtractionRule = {IntegerSubtraction.class, IntegerOperation.class};
		addRule(TokenType.MINUS, subtractionRule);
		rulesDoNotPoll = true;
	}

}
