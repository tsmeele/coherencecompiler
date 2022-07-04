package nl.tsmeele.grammar;

/**
 * Enumerated list of lexical constructs and their regular expression pattern.
 * Patterns should assume that a match with input data is tried in ordinal sequence of token type.
 * @author Ton Smeele
 *
 */
public enum TokenType {
	PROTOCOL("protocol"),
	
	BRACKETOPEN("[(]"),	//  we use [] to wrap characters that may have special meaning in regexp 
	BRACKETCLOSE("[)]"),
	CURLYOPEN("[{]"),
	CURLYCLOSE("[}]"),
	SEMICOLON("[;]"),
	COMMA("[,]"),
	EQUALS("[=]"),
	WHITESPACE("[\\s]+"),
	LET("let"),
	FROM("from"),
	TO("to"),
	WITH("with"),
	THREADS("threads"),
	WHILE("while"),
	NOT("not"),
	DONE("done"),
	PARALLEL("parallel"),
	AND("and"),
	ATOMIC("atomic"),

	
	INTEGERLITERAL("[-]{0,1}\\d+"),
	
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
	PLUS("[+]"),
	
	
	// last pattern that catches the remaining full line
	UNSUPPORTED("2.*");
	
	public final String pattern;
	
	private TokenType(String pattern) {
			this.pattern = pattern;
		}

}
