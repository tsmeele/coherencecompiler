package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class ThenClause extends Term {

	@SuppressWarnings("rawtypes")
	public ThenClause() {
		super();
		Class[] rule = {StatementBlock.class};
		addRule(TokenType.TEXT_THEN, rule);
	}
}
