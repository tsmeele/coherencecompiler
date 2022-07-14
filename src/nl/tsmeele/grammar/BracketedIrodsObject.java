package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class BracketedIrodsObject extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public BracketedIrodsObject() {
		super();
		Class[] rule = {IrodsObject.class};
		addRule(TokenType.BRACKETOPEN, rule);
		closingTokenType = TokenType.BRACKETCLOSE;
	}

}
