package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class CoherentStatement extends Term {

	@SuppressWarnings("rawtypes")
	public CoherentStatement() {
		super();
		Class[] rule = {Role.class, MoreRoles.class};
		addRule(TokenType.TEXT_COHERENT, rule);
		closingTokenType = TokenType.SEMICOLON;
		addFunction(StackableFunctionType.COHERENT);
	}
}
