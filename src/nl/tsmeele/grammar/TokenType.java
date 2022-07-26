package nl.tsmeele.grammar;

/**
 * Enumerated list of lexical constructs and their regular expression pattern.
 * Patterns should assume that a match with input data is tried in ordinal sequence of token type.
 * @author Ton Smeele
 *
 */
public enum TokenType {
	TEXT_PROTOCOL("protocol"),
	
	BRACKETOPEN("[(]"),	//  we use [] to wrap characters that may have special meaning in regexp 
	BRACKETCLOSE("[)]"),
	CURLYOPEN("[{]"),
	CURLYCLOSE("[}]"),
	SEMICOLON("[;]"),
	COMMA("[,]"),
	EQUALS("[=]"),
	WHITESPACE("[\\s]+"),
	TEXT_LET("let"),
	TEXT_FROM("from"),
	TEXT_TO("to"),
	TEXT_PROTECTS("protects"),
	TEXT_WITH("with"),
	TEXT_THREADS("threads"),
	TEXT_WHILE("while"),
	TEXT_NOT("not"),
	TEXT_DONE("done"),
	TEXT_PARALLEL("parallel"),
	TEXT_AND("and"),
	TEXT_ATOMIC("atomic"),
	TEXT_COHERENT("coherent"),

	
	//INTEGERLITERAL("[-]{0,1}\\d+"),
	INTEGERLITERAL("\\d+"),
	
	// literalstring is delimited with either single or double quotes 
	// https://stackoverflow.com/questions/2498635/java-regex-for-matching-quoted-string-with-escaped-quotes
	SQSTRINGLITERAL(
			// single-quoted string
			"'([^\\\\']+|" + 				// regular characters
	        "[\']{2,2}|" +					// or repeat-style escaped single quote 
			"\\\\([btnfr\"'\\\\]|" +		// or backslash-style escaped character sequence
	        "[0-3]?[0-7]{1,2}|" +
			"u[0-9a-fA-F]{4}))*'"),
	DQSTRINGLITERAL(
	        // double-quoted string (almost similar to above)
	        "\"([^\\\\\"]+|" +
	        "[\"]{2,2}|" +			
			"\\\\([btnfr\"'\\\\]|" +
	        "[0-3]?[0-7]{1,2}|" +
			"u[0-9a-fA-F]{4}))*\""),
	IDENTIFIER("[a-zA-Z_$][a-zA-Z\\d_$]*"),
	COMMENT("//.*"),
	
	MINUS("[-]"),  // must have lower precedence than LITERALINTEGER
	PLUS("[+]");
	

	
	public final String pattern;
	
	
	private TokenType(String pattern) {
		this.pattern = pattern;
	}
	
	public static boolean isVariable(TokenType tt) {
		return tt == IDENTIFIER;
	}
	
	public static boolean isValue(TokenType tt) {
		return tt == INTEGERLITERAL ||
				tt == SQSTRINGLITERAL ||
				tt == DQSTRINGLITERAL;
	}
	
	public static boolean isStrippable(TokenType tt) {
		return tt ==  COMMENT || tt == WHITESPACE;
	}

}
