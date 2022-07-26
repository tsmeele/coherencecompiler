package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IntegerOperandBracketed extends Term {

	@SuppressWarnings("rawtypes")
	public IntegerOperandBracketed()  {
		super();
		Class[] rule = {IntegerExpression.class};
		addRule(TokenType.BRACKETOPEN, rule);
		closingTokenType = TokenType.BRACKETCLOSE;

	}

}
