package nl.tsmeele.compiler;

public abstract class OptionalTerm extends Term {

	public OptionalTerm() {
		super();
		rulesAreOptional = true;
	}
}
