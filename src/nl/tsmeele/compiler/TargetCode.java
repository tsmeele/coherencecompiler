package nl.tsmeele.compiler;


/**
 * TargetCode represents a fragment of code produced by a code generator (function) upon evaluation of a Term.
 * 
 * @author Ton Smeele
 *
 */
public interface TargetCode {

	/**
	 * renders the target code suitable for output to a textfile
	 * @return returns the code, or null if the code represents a result that produces no code
	 */
	public String renderAsString(); 
	
	
}
