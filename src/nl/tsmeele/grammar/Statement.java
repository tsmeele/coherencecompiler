package nl.tsmeele.grammar;


import nl.tsmeele.compiler.NonConsumingTerm;

public class Statement extends NonConsumingTerm {

	@SuppressWarnings("rawtypes")
	public Statement() {
		super();
		
		Class[] coherentRule = {CoherentStatement.class};
		addRule(TokenType.TEXT_COHERENT, coherentRule);
		
		Class[] atomicRule = {AtomicStatement.class};
		addRule(TokenType.TEXT_ATOMIC, atomicRule);
		
		Class[] communicationRule = {CommunicationStatement.class};
		addRule(TokenType.TEXT_FROM, communicationRule);
		
		// 'empty' statement:
		addRule(TokenType.SEMICOLON, null);
		// block without any statements at all:
		addRule(TokenType.CURLYCLOSE, null);
	}
	


}
