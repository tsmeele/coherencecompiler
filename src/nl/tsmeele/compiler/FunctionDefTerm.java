package nl.tsmeele.compiler;

/**
 * FunctionDefTerm is a type of Term that defines an abstraction. 
 * Classes that extend this class should have a single rule that
 * takes a TokenType tt that meets TokenType.isVariable(tt) == true.  
 * The associated variable will be considered the name of a function.
 * 
 * @author Ton Smeele
 *
 */
public abstract class FunctionDefTerm extends Term {

	public FunctionDefTerm() {
		super();
		definesAbstraction = true;
		rulesDoNotPoll = false;
		rulesAreOptional = false;
	}
}
