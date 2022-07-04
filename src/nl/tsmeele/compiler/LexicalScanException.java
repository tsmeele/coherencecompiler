package nl.tsmeele.compiler;

public class LexicalScanException extends RuntimeException {
	private static final long serialVersionUID = 5471015923567338767L;

	public LexicalScanException(int lineNo, int columnNo, String source) {
		super( 	"ERROR: Unsupported input data type" +
				" in line "   + Integer.toString(lineNo) + 
				" at column " + Integer.toString(columnNo) + 
				": \"" + source + "\""
		);
	}
	

}
