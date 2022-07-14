package nl.tsmeele.compiler;

public abstract class NonConsumingTerm extends Term {
	
	public NonConsumingTerm() {
		super();
		rulesDoNotPoll = true;
	}

}
