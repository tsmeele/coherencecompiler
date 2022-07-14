package nl.tsmeele.compiler;

public class NonConsumingOptionalTerm extends Term {

	public NonConsumingOptionalTerm() {
		super();
		rulesDoNotPoll = true;
		rulesAreOptional = true;
	}
}
