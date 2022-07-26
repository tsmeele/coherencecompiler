package nl.tsmeele.grammar;


import nl.tsmeele.compiler.Term;

public class Participants extends Term {

	@SuppressWarnings("rawtypes")
	public Participants() {
		super();
		Class[] c = {Role.class, MoreRoles.class};
		addRule(TokenType.BRACKETOPEN, c);	
		closingTokenType = TokenType.BRACKETCLOSE;
		addFunction(StackableFunctionType.PARTICIPANTS);
	}
	

}
