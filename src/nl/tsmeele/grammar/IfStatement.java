package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class IfStatement extends Term {
	
	@SuppressWarnings("rawtypes")
	public IfStatement() {
		super();
		Class[] rule = {IfClause.class, ThenClause.class, ElseClause.class};
		addRule(TokenType.TEXT_IF, rule);
		//closingTokenType = TokenType.SEMICOLON;
		addFunction(StackableFunctionType.IFTHENELSE);
	}
}
