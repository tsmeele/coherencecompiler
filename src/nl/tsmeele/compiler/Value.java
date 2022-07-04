package nl.tsmeele.compiler;

import java.util.Stack;

public class Value extends Stackable {
	
	private enum ValueType {
		STRING, INTEGER;
	}
	
	private ValueType type = null;
	private String text = null;
	private int number = 0;
	
	public Value(String quotedText) {
		super(StackItemType.VALUE);
		this.type = ValueType.STRING;
		if (quotedText.length() >= 2 && 
			isQuoted(quotedText.charAt(0),quotedText.charAt(quotedText.length()-1) )) {
			text = quotedText.substring(1, quotedText.length() - 1);
		} else {
			// TODO: shouldn't we throw an IllegalValue exception?
			text = quotedText;
		}
		try {
			this.number = Integer.valueOf(text);
		}
		catch (NumberFormatException e) {
			this.number = 0;
		}
	}
	
	public Value(int number) {
		super(StackItemType.VALUE);
		this.type = ValueType.INTEGER;
		this.number = number;
		this.text = String.valueOf(number);
	}
	
	public String toString() {
		return text;
	}
	
	public boolean isString() {
		return type == ValueType.STRING;
	}
	
	public boolean isInteger() {
		return type == ValueType.INTEGER;
	}
	
	
	public String getString() {
		return text;
	}
	
	public int getInteger() {
		return number;
	}

	private boolean isQuoted(char c1, char c2) {
		return c1 == c2 && (c1 == '"' || c1 == '\'');
	}

	
	
}
