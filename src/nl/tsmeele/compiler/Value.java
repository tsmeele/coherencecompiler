package nl.tsmeele.compiler;

import java.util.LinkedList;


/**
 * Value can hold a single value of any type. It will attempt to translate the
 * value to a requested target type whenever its data is queried.
 * 
 * String literals "true" and "false" are projected to integers 1 and 0.
 * 
 * @author Ton Smeele
 *
 */
public class Value extends Stackable {

	private enum ValueType {
		STRING, INTEGER, TARGETCODE,TERM, VARIABLE;
	}

	private ValueType type = null;
	private String text = null;
	private int number = 0;
	private LinkedList<TargetCode> code = null;
	private Term term = null;
	private Variable variable = null;

	public Value(String quotedText) {
		super(StackItemType.VALUE);
		this.type = ValueType.STRING;
		if (quotedText.length() >= 2 && isQuoted(quotedText.charAt(0), quotedText.charAt(quotedText.length() - 1))) {
			text = quotedText.substring(1, quotedText.length() - 1);
		} else {
			// no matching quotes found, we will assume the input text is not quoted
			text = quotedText;
		}
	}

	public Value(int number) {
		super(StackItemType.VALUE);
		this.type = ValueType.INTEGER;
		this.number = number;
	}
	
	public Value(TargetCode fragment) {
		super(StackItemType.VALUE);
		this.type = ValueType.TARGETCODE;
		code = new LinkedList<TargetCode>();
		code.addLast(fragment);
	}

	public Value(Term term) {
		super(StackItemType.VALUE);
		this.type = ValueType.TERM;
		this.term = term;
	}
	
	public Value(Variable variable) {
		super(StackItemType.VALUE);
		this.type = ValueType.VARIABLE;
		this.variable = variable;
	}
	
	public String toString() {
		switch (type) {
		case INTEGER:	return String.valueOf(number);
		case STRING:	return text;
		case TARGETCODE:	return code.toString();
		case TERM:	return term.toString();
		case VARIABLE: return variable.toString();
		}
		return text;
	}

	public String getString() {
		return toString();
	}

	public int getInteger() {
		switch (type) {
		case INTEGER: 	return number;
		case TARGETCODE: 
		case VARIABLE:
		case TERM: 		return 0;
		case STRING:
		}
		if (text.equalsIgnoreCase("true")) {
			return 1;
		}
		if (text.equalsIgnoreCase("false")) {
			return 0;
		}
		try {
			this.number = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			return 0;
		}
		return number;
	}
	
	public LinkedList<TargetCode> getCode() {
		return code;
	}
	
	public Term getTerm() {
		return term;
	}
	
	public Variable getVariable() {
		return variable;
	}
	
	public String getValueType() {
		return type.toString();
	}
	
	public boolean isString() {
		return type == ValueType.STRING;
	}

	public boolean isInteger() {
		return type == ValueType.INTEGER;
	}
	
	public boolean isCode() {
		return type == ValueType.TARGETCODE;
	}
	
	public boolean isTerm() {
		return type == ValueType.TERM;
	}
	
	public boolean isVariable() {
		return type == ValueType.VARIABLE;
	}


	
	public Value addLast(TargetCode tc) {
		code.addLast(tc);
		return this;
	}
	
	public Value addLast(LinkedList<TargetCode> tcList) {
		code.addAll(tcList);
		return this;
	}
	
	public Value addFirst(TargetCode tc) {
		code.addFirst(tc);
		return this;
	}
	
	public Value addFirst(LinkedList<TargetCode> tcList) {
		LinkedList<TargetCode> resultList = new LinkedList<TargetCode>();
		resultList.addAll(tcList);
		resultList.addAll(code);
		code = resultList;
		return this;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass()) return false;
		Value vObj = (Value) obj;
		if (type != vObj.type) return false; 
		switch (type) {
		case INTEGER: return number == vObj.number;
		case STRING: return text.equals(vObj.text);
		case TERM: return term.equals(vObj.term);
		case VARIABLE: return variable.equals(vObj.variable);
		case TARGETCODE:
		}
		if (code.size() != vObj.code.size()) return false;
		for (int i = 0; i < code.size(); i++ ) {
			if (!code.get(i).equals(vObj.code.get(i)) ) {
				return false;
			}
		}
		return true;
	}


	private boolean isQuoted(char c1, char c2) {
		return c1 == c2 && (c1 == '"' || c1 == '\'');
	}

}
