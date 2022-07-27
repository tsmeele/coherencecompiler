package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class AtomicStatement extends Term {
	
	@SuppressWarnings("rawtypes")
	public AtomicStatement() {
		super();
		Class[] atomicRule = {IrodsOperation.class, FromRole.class, ProtectsRole.class, AtomicBlock.class};
		addRule(TokenType.TEXT_ATOMIC, atomicRule);
		addFunction(StackableFunctionType.ATOMIC);
	}
}
