package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class ElseClause extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public ElseClause() {
		super();
		Class[] rule = {StatementBlock.class};
		addRule(TokenType.TEXT_ELSE, rule);
	}
}
