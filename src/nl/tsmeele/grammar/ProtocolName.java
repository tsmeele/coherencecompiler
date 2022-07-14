package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class ProtocolName extends Term {

	public ProtocolName() {
		super();
		addRule(TokenType.IDENTIFIER, null);
		addFunction(StackableFunctionType.PROTOCOL);
	}


}
