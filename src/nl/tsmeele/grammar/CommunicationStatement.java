package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;
import nl.tsmeele.generator.common.StackableFunctionType;

public class CommunicationStatement extends TermOfTerms {

	@SuppressWarnings("rawtypes")
	public CommunicationStatement() {
		Class[] commRule = { FromRole.class, ToRole.class, WithClause.class, Message.class};
		addDefaultRule(commRule);
		addFunction(StackableFunctionType.COMMUNICATION);
	}

}
