package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class MoreCoherentObjects extends OptionalTerm {
	
	@SuppressWarnings("rawtypes")
	public MoreCoherentObjects() {
		super();
		Class[] rule = {CoherentObject.class, MoreCoherentObjects.class};
		addRule(TokenType.COMMA, rule);
	}
}
