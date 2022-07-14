package nl.tsmeele.grammar;

import nl.tsmeele.compiler.Term;

public class AtomicStatement extends Term {
	
	public AtomicStatement() {
		super();
		Class[] atomicRule = {AtomicName.class, AtomicBlock.class};
		addRule(TokenType.TEXT_ATOMIC, atomicRule);
	}
}
