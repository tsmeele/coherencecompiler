package nl.tsmeele.grammar;

import nl.tsmeele.compiler.OptionalTerm;

public class MoreStatements extends OptionalTerm {

	@SuppressWarnings("rawtypes")
	public MoreStatements() {
		super();
		Class[] statement = { Statement.class, MoreStatements.class};
		addRule(TokenType.SEMICOLON, statement);

	}

}
