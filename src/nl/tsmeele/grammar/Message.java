package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class Message extends Term {

	@SuppressWarnings("rawtypes")
	public Message() {
		super();
		Class[] messageRule = {BracketedIrodsObject.class};
		addRule(TokenType.IDENTIFIER, messageRule);
		addFunction(StackableFunctionType.MESSAGE);

	}

}
