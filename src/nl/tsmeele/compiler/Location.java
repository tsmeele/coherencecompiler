package nl.tsmeele.compiler;

/**
 * Keeps the source line/column location of a token that was read.
 * 
 * @author Ton Smeele
 *
 */
public class Location {
	private int lineNo = 0;
	private int columnNo = 0;

	public Location(int lineNo, int columnNo) {
		this.lineNo = lineNo;
		this.columnNo = columnNo;
	}

	public String toString() {
		return "[" + lineNo + "," + columnNo + "]";
	}

	public int getLine() {
		return lineNo;
	}

	public int getColumn() {
		return columnNo;
	}
}
