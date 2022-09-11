package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class DottedIrodsAttribute extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public DottedIrodsAttribute() {
		super();
		Class[] rule = {IrodsAttribute.class};
		addRule(TokenType.DOT, rule);
	}

}
