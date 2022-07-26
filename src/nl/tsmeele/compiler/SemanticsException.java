package nl.tsmeele.compiler;

public class SemanticsException extends RuntimeException {
	private static final long serialVersionUID = -2583559883696918568L;

	public SemanticsException(AST ast, String message) {
		super("Semantic Error at " + ast.toString() + " " + message);
	}

}
