package nl.tsmeele.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.tsmeele.grammar.TokenType;

/**
 * LexicalScanner is a filter that processes a text input stream to produce a
 * token list.
 * 
 * @author Ton Smeele
 *
 */
public class LexicalScanner {
	private HashMap<TokenType, Pattern> patternsTable = new HashMap<TokenType, Pattern>();
	private boolean useEclipseConsole = true;

	public LexicalScanner() {
		initPatternsTable();
	}

	public TokenList scan(BufferedReader reader) throws IOException, LexicalScanException {
		TokenList tokenList = new TokenList();
		String line;
		int lineNo = 0;
		line = reader.readLine();
		while (line != null) {
			lineNo++;
			// workaround as Eclipse will never return end of file for console
			if (useEclipseConsole && line.equals("EOF")) {
				return tokenList;
			}
			tokenList.addAll(scanLine(lineNo, line));
			line = reader.readLine();
		}
		return tokenList;
	}

	private TokenList scanLine(int lineNo, String line) throws LexicalScanException {
		TokenList tokens = new TokenList();
		if (line == null) {
			return tokens;
		}
		int columnNo = 0;
		while (line.length() > 0) {
			String source = "";
			TokenType tt = null;
			// we choose the match longest in length
			for (TokenType tType : TokenType.values()) {
				Matcher m = patternsTable.get(tType).matcher(line);
				if (m.find() && m.group(0).length() > source.length()) {
					source = m.group(0);
					tt = tType;
				}
			}
			if (source.length() == 0) {
				throw new LexicalScanException(lineNo, columnNo, line);
			}
			tokens.add(new Token(tt, source, new Location(lineNo, columnNo)));
			line = line.substring(source.length());
			columnNo = columnNo + source.length();
		}
		return tokens;
	}

	/**
	 * compiles token type regular expressions so that these patterns can be matched
	 * efficiently
	 */
	private void initPatternsTable() {
		for (TokenType tt : TokenType.values()) {
			Pattern compiled = Pattern.compile("^(" + tt.pattern + ")"); // match only at start of string
			patternsTable.put(tt, compiled);
		}
	}

}
