package nl.tsmeele.grammar;

public enum StackableFunctionType {
	// expressions, these functions are expected to return (at most) a single value
	INTEGER_ADDITION, INTEGER_SUBTRACTION, WITH_THREADS, ROLE, PARTICIPANTS, PROTOCOL,  

	
	// "statements", these functions are expected to return a target code value
	 COMMUNICATION, ATOMIC_BLOCK, ATOMIC, COHERENT, STATEMENT_BLOCK;
	
	
	
	
	
}
