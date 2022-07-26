package nl.tsmeele.compiler;

import java.util.LinkedList;

/**
 * TargetCode represents a fragment of code produced by a code generator (function) upon evaluation of a Term.
 * 
 * @author Ton Smeele
 *
 */
public interface TargetCode {

	public String renderAsString(); 
	
}
