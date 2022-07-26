package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class BracketedIrodsAttribute extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public BracketedIrodsAttribute() {
		super();
		Class[] rule = {IrodsAttribute.class};
		addRule(TokenType.BRACKETOPEN, rule);
		closingTokenType = TokenType.BRACKETCLOSE;
	}

}
