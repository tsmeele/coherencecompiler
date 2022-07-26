package nl.tsmeele.compiler;

import java.util.ArrayList;
import java.util.Iterator;

import nl.tsmeele.grammar.TokenType;

/**
 * Class TermOfTerms allows to describe a term solely in terms of other terms,
 * without specifying terminal symbols. 
 * Example use case: <IntegerExpression> ::= <Integeroperand> <IntegerOperation>
 * 
 * Consequences: 1) this term cannot be defined as 'optional' (although its
 * subterms can be optional) 2) this term does not consume any tokens upon
 * parsing (except for closingTokenType) 3) rules other than the default rule
 * are ignored
 * 
 * @author Ton Smeele
 *
 */
public abstract class TermOfTerms extends Term {
	protected ArrayList<Class<? extends Term>> defaultRule = null;

	protected void addDefaultRule(@SuppressWarnings("rawtypes") Class[] classList) {
		defaultRule = makeProduction(classList);
	}

	@Override
	public String toSyntax() {
		String text = toBNF() + " ::=";
		Iterator<Class<? extends Term>> it = defaultRule.iterator();
		while (it.hasNext()) {
			Class<? extends Term> t = it.next();
			Term subTerm = instantiateRuntimeClass(t);
			text = text.concat(" " + subTerm.toOptionalBNF());
		}
		if (closingTokenType != null) {
			text = text.concat(" " + closingTokenType);
		}
		return text + showFunction();
	}

	@Override
	protected void collectTermSubClasses(ArrayList<Class<? extends Term>> termSet) {
		if (defaultRule == null)
			return;
		for (Class<? extends Term> t : defaultRule) {
			if (!termSet.contains(t)) {
				termSet.add(t);
				Term term = instantiateRuntimeClass(t);
				term.collectTermSubClasses(termSet);
			}
		}
	}

	@Override
	public AST parse(TokenList tokens) {
		AST ast = new AST(this);
		if (defaultRule == null) {
			throw new ParseException("Rule is missing, don't know how to parse term " + name);
		}
		for (Class<? extends Term> t : defaultRule) {
			// for the subtree we need a fresh, similar type, Term instance, so that we can
			// populate it with data
			Term term = instantiateRuntimeClass(t);
			ast.addChild(term.parse(tokens));
		}
		
		// finally we check the existence of a constant-type closing symbol
		// if present, this symbol is consumed.
		if (closingTokenType == null)
			return ast;
		if (closingTokenType != null && (TokenType.isValue(closingTokenType) || TokenType.isVariable(closingTokenType)))
			// closing token should be a constant, e.g. a keyword or a ')' symbol
			throw new ParseException("ClosingTokenType must be constant-type token");
		Token token = tokens.pollFirst();
		if (!token.getType().equals(closingTokenType))
			throw new ParseException(closingTokenType, tokens);
		return ast;
	}

}
