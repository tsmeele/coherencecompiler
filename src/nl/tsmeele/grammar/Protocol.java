package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class Protocol extends Term {

	@SuppressWarnings("rawtypes")
	public Protocol() {
		super();
		Class[] c = {ProtocolName.class, RoleList.class, StatementBlock.class};
		addRule(TokenType.TEXT_PROTOCOL, c);
	

	}


	

}
