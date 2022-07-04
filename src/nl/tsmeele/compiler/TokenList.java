package nl.tsmeele.compiler;

import java.util.Iterator;
import java.util.LinkedList;

import nl.tsmeele.grammar.TokenType;

public class TokenList extends LinkedList<Token>  {
	private static final long serialVersionUID = -8697325975707157127L;

	private static final TokenType[] stripList = {TokenType.COMMENT, TokenType.WHITESPACE};	

	
	public String toMultiLineString() {
		String text = "[\n";
		for (Token t: this) {
			text = text.concat(" " + t.toString() + "\n");
		}
		return text + "]";
	}
	
	public String toString() {
		String text = "[";
		for (int i = 0; i< size(); i++) {
			if (i != 0) {
				text = text.concat(", ");
			}
			text = text.concat(get(i).toString());
		}
		return text + "]";
	}
	
	public Token headTestAndPoll(TokenType tokenType) {
		testHead(tokenType);
		return pollFirst();
	}
	
	public void testHead(TokenType tokenType) throws ParseException  {
		if (!head(tokenType)) 
			throw new ParseException(tokenType, this.get(0));
	}
	
	public boolean head(TokenType tokenType) {
		return !isEmpty() && get(0).getType() == tokenType;
	}
	
	public boolean at(int index, TokenType tokenType) {
		return index < size() && get(index).getType() == tokenType;
	}
	
	
	
	public TokenList extractStripped() {
		TokenList tokensOut = new TokenList();
		Iterator<Token> it = iterator();
		while (it.hasNext()) {
			Token t = it.next();
			if (!canStrip(t.getType())) {
				tokensOut.add(t);
			}
		}
		return tokensOut;
	}
	
	private boolean canStrip(TokenType t) {
		for (int i=0; i < stripList.length; i++) {
			if (t == stripList[i]) return true;
		}
		return false;
	}
	
	
	
}
