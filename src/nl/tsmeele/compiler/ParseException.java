package nl.tsmeele.compiler;

import nl.tsmeele.grammar.TokenType;

public class ParseException extends RuntimeException {
	private static final long serialVersionUID = -2652473504201354008L;

	public ParseException(TokenType expected, TokenList tokens) {
		super("Syntax error: expected " + expected + tokenText(tokens));
	}
	
	public ParseException(TokenType expected, Token token) {
		super("Syntax error: expected " + expected + tokenText(token));
	}
	
	public ParseException(String expected, TokenList tokens) {
		super("Syntax error: expected " + expected + tokenText(tokens));
	}
	
	
	
	private static String tokenText(TokenList tokens) {
		if (tokens == null || tokens.isEmpty()) return "";
		return ", received: " + tokens.get(0).toString();
	}
	
	private static String tokenText(Token token) {
		if (token == null) return "";
		return ", received: " + token.toString();
	}

}
