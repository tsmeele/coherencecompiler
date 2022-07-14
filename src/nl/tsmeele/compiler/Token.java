package nl.tsmeele.compiler;

import nl.tsmeele.grammar.TokenType;

public class Token {
	
	private TokenType tokenType = null;
	private String source = "";
	private Location location = null;
	

	public Token(TokenType tokenType, String source, Location location) {
		this.tokenType = tokenType;
		this.source = source;
		this.location = location;
	}

	public String toString() {
		return location.toString() + getType().toString() + ": \"" + source + "\"" ;
	}
	
	public TokenType getType() {
		return tokenType;
	}
	
	public String getSource() {
		return source;
	}
	
	public Location getLocation() {
		return location;
	}
	

	

	
}
