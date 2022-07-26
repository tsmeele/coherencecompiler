package nl.tsmeele.compiler;

import java.util.Iterator;
import java.util.LinkedList;

import nl.tsmeele.grammar.TokenType;

/**
 * TokenList maintains a list of Tokens.
 * 
 * @author Ton Smeele
 *
 */
public class TokenList extends LinkedList<Token> {
	private static final long serialVersionUID = -8697325975707157127L;

	public String toMultiLineString() {
		String text = "[\n";
		for (Token t : this) {
			text = text.concat(" " + t.toString() + "\n");
		}
		return text + "]";
	}

	public String toString() {
		String text = "[";
		for (int i = 0; i < size(); i++) {
			if (i != 0) {
				text = text.concat(", ");
			}
			text = text.concat(get(i).toString());
		}
		return text + "]";
	}

	public TokenList extractStripped() {
		TokenList tokensOut = new TokenList();
		Iterator<Token> it = iterator();
		while (it.hasNext()) {
			Token t = it.next();
			if (!TokenType.isStrippable(t.getType())) {
				tokensOut.add(t);
			}
		}
		return tokensOut;
	}

}
