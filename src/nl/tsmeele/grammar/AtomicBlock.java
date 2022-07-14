package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;
import nl.tsmeele.generator.common.StackableFunctionType;

public class AtomicBlock extends Term {
	
	@SuppressWarnings("rawtypes")
	public AtomicBlock() {
		super();
		Class[] c = {Statement.class, MoreStatements.class};
		addRule(TokenType.CURLYOPEN, c);
		closingTokenType = TokenType.CURLYCLOSE;
		addFunction(StackableFunctionType.ATOMIC_EXIT);
	}

}
