package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class DottedAttribute extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public DottedAttribute() {
		super();
		Class[] rule = {Attribute.class};
		addRule(TokenType.DOT, rule);
	}

}
