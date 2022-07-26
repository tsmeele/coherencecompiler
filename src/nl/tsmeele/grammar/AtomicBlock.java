package nl.tsmeele.grammar;

import nl.tsmeele.compiler.TermOfTerms;

/**
 * AtomicBlock wraps a StatementBlock to facilitate a function  that implements lock/unlock operations.
 * 
 * @author Ton Smeele
 *
 */
public class AtomicBlock extends TermOfTerms {
	
	@SuppressWarnings("rawtypes")
	public AtomicBlock() {
		super();
		Class[] c = {StatementBlock.class};
		addDefaultRule(c);
		addFunction(StackableFunctionType.ATOMIC_BLOCK);
	}


}
