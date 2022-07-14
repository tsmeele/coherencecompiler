package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class BracketedIntegerOperand extends Term {

	@SuppressWarnings("rawtypes")
	public BracketedIntegerOperand()  {
		super();
		Class[] rule = {IntegerExpression.class};
		addRule(TokenType.BRACKETOPEN, rule);
		closingTokenType = TokenType.BRACKETCLOSE;

	}

}
