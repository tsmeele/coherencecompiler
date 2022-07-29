package nl.tsmeele.grammar;


import nl.tsmeele.compiler.NonConsumingTerm;

public class Statements extends NonConsumingTerm {

	@SuppressWarnings("rawtypes")
	public Statements() {
		super();
		
		Class[] coherentRule = {CoherentStatement.class, Statements.class};
		addRule(TokenType.TEXT_COHERENT, coherentRule);
		
		Class[] atomicRule = {AtomicStatement.class, Statements.class};
		addRule(TokenType.TEXT_ATOMIC, atomicRule);
		
		Class[] communicationRule = {CommunicationStatement.class, Statements.class};
		addRule(TokenType.TEXT_FROM, communicationRule);
		
		Class[] ifRule = {IfStatement.class, Statements.class};
		addRule(TokenType.TEXT_IF, ifRule);
		
		// 'empty' statement:
		Class[] emptyRule = {EmptyStatement.class, Statements.class};
		addRule(TokenType.SEMICOLON, emptyRule);
		// block without any statements at all:
		addRule(TokenType.CURLYCLOSE, null);
	}
	


}
