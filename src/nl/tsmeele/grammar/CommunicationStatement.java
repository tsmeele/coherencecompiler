package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;

public class CommunicationStatement extends TermOfTerms {

	@SuppressWarnings("rawtypes")
	public CommunicationStatement() {
		Class[] commRule = { FromRole.class, ToRole.class, WithClause.class, IrodsOperation.class};
		addDefaultRule(commRule);
		closingTokenType = TokenType.SEMICOLON;
		addFunction(StackableFunctionType.COMMUNICATION);
	}

}
