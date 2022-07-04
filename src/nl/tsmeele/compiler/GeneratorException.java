package nl.tsmeele.compiler;

public class GeneratorException extends RuntimeException {
	private static final long serialVersionUID = -5135230397720216973L;

	public GeneratorException(String message) {
		super("Code generator error: " + message);
	}

}
