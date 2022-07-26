package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class Protocol extends Term {

	@SuppressWarnings("rawtypes")
	public Protocol() {
		super();
		Class[] c = {ProtocolName.class, Participants.class, StatementBlock.class};
		addRule(TokenType.TEXT_PROTOCOL, c);
		addFunction(StackableFunctionType.PROTOCOL);
	}


	

}
