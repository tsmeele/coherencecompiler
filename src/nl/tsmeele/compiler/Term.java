package nl.tsmeele.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nl.tsmeele.grammar.StackableFunctionType;
import nl.tsmeele.grammar.TokenType;


/**
 * A Term represent a language syntax construct. Terms can be used to parse a list of tokens into an abstract syntax tree, an AST 
 * (which will consist of a hierarchy of terms). The AST of terms can be semantically analyzed in particular to establish that language
 * variables are declared before they are being used anywhere in the token source input. 
 * Lastly, the AST of terms can be evaluated hence effectively executed in sequence. Evaluation is used to generate output code.
 * During evaluation, any occurrence of a language variable or literal value will be added to a stack.
 * Terms may specify a 'function', which can inspect the stack to read the produced variables and literals. 
 * The function uses this input to generate output code. 
 * The terms only reference functions by name, the function implementation can differ across code generators. 
 * 
 * In general, a term is defined by a set of rules. Each rule comprises of a start token and a sequence of zero or more terms.  
 * The start token defines which input token is allowed next to be consumed. Choice is possible by defining multiple rules, each
 * with a different start token.  The sequence of terms will be used to define the subsequent parse actions.
 * Example term:  rule1 = BRACKET_OPEN <expression>   
 *                rule2 = CURLY_OPEN <statementblock>
 * In this case, the source input must be either a bracket open, or a curly open character. In case of the bracket open, the next
 * action will be specified in the term <expression>.  
 *  
 * Several subclasses of terms are supported, to facilitate a variety of language constructs:
 *     - optional 		: If none of the start tokens match, consider the term done rather then raise a parse error
 *                        example:  <moreRoles> ::= [ COMMA <role> <moreRoles> ]
 *     - non-consuming	: After matching the start token, the input source is not advanced to the next token. This allows the
 *                        next term in sequence to process the start token again.
 *                        Non consuming terms can be used to provide a choice of alternative terms.
 *                        example:  <statement> ::= <communication> | <declaration> | <whileLoop>
 *	   - term-of-terms	: Instead of having a start token, a single rule with only a sequence of terms is defined. This merely
 *                        facilitates an abstraction level.
 *                        example:  <protocol> ::= <protocolHeader> <roleList> <statementBlock>
 * 
 * All subclasses except the term-of-terms can support an optional closing token which then must match after the sequence of terms
 * has been processed.
 * For example:  <statementBlock> ::= CURLYOPEN <statements> CURLYCLOSE
 * 
 * In addition to the above, there is a term subclass that provides a combination of optional and non-consuming behavior. 
 * This subclass can be used to facilitate an optional clause in a statement, for example:  
 *       <withThreads> ::= [ WITH <threadsSpecification> ]
 *       <threadsSpecifiction> ::= WITH <expression> THREADS
 * 
 * @author Ton Smeele
 *
 */
public abstract class Term  {
	final static boolean DEBUG = false;
	protected String name = getClass().getSimpleName();
	protected HashMap<TokenType, ArrayList<Class<? extends Term>>> rules = new HashMap<TokenType, ArrayList<Class<? extends Term>>>();

	// properties that can be overridden by subclass constructors:
	protected boolean rulesDoNotPoll = false;
	protected boolean rulesAreOptional = false;
	protected boolean variableRequiresPriorDeclaration = false;
	protected TokenType closingTokenType = null;
	protected boolean definesAbstraction = false;
	protected String nameSpace = null; // explicitly specify a namespace for variables (otherwise the term class name
										// is used)
	private StackableFunctionType functionType = null;

	// other properties, used internally by Term:
	private ArrayList<Variable> variableTable = null;
	private Stackable data = null;
	private StackableFunction function = null;

	public String toString() {
		String dataInfo = data == null ? "" : " '" + data.toString() + "'";
		String tableInfo = variableTable == null ? "" : " " + variableTable;
		return name + dataInfo + tableInfo;
	}
	
	public String getName() {
		return name;
	}

	// returns a BNF style syntax for the subset of the language starting at this
	// term
	public String toAllSyntax() {
		ArrayList<Class<? extends Term>> members = new ArrayList<Class<? extends Term>>();
		collectTermSubClasses(members);
		String syntaxText = toSyntax() + "\n";
		for (Class<? extends Term> c : members) {
			Term term = instantiateRuntimeClass(c);
			syntaxText = syntaxText.concat(term.toSyntax() + "\n");
		}
		return syntaxText;
	}

	// returns a BNF style syntax for this Term
	public String toSyntax() {
		String text = toBNF() + " ::=";
		Iterator<TokenType> it = rules.keySet().iterator();
		while (it.hasNext()) {
			TokenType tt = it.next();
			ArrayList<Class<? extends Term>> terms = rules.get(tt);
			if (!rulesDoNotPoll) {
				text = text.concat(" " + tt);
			} 
			if (terms.isEmpty()) {
				text = text.concat(" -");
			}
			for (Class<? extends Term> t : terms) {
				Term subTerm = instantiateRuntimeClass(t);
				text = text.concat(" " + subTerm.toOptionalBNF());
			}
			if (it.hasNext()) {
				text = text.concat(" |");
			}
		}
		if (closingTokenType != null) {
			text = text.concat(" " + closingTokenType);
		}
		return text + showFunction();
	}

	public String showFunction() {
		return functionType == null ? "" : "  + f:" + functionType + "()";
	}

	// variable table related methods

	protected void createVariableTable() {
		variableTable = new ArrayList<Variable>();
	}

	protected boolean registerVariable(AST ast, Variable v) {
		while (ast != null) {
			Term term = ast.get();
			if (term.hasVariableTable()) {
				term.addVariable(v);
				return true;
			}
			ast = ast.getParent();
		}
		return false;
	}

	protected boolean isRegisteredVariable(AST ast, Variable v) {
		while (ast != null) {
			Term term = ast.get();
			if (term.hasVariable(v))
				return true;
			ast = ast.getParent();
		}
		return false;
	}

	private boolean hasVariableTable() {
		return variableTable != null;
	}

	private boolean hasVariable(Variable v) {
		return hasVariableTable() && variableTable.contains(v);
	}

	private void addVariable(Variable v) {
		if (!variableTable.contains(v)) {
			variableTable.add(v);
		}
	}

	public AST parse(TokenList tokens) {
		AST ast = new AST(this);
		if (tokens.isEmpty()) {
			if (rulesAreOptional)
				return ast;
			throw new ParseException(name + ", yet reached end of source input", tokens);
		}

		// token type is matched against production rule patterns suitable for this Term
		Token token = tokens.peekFirst();
		ArrayList<Class<? extends Term>> matchedRule = rules.get(token.getType());
		if (matchedRule == null) {
			if (rulesAreOptional) {
				return ast;
			} else {
				throw new ParseException(name, tokens);
			}
		}

		// Process the matching rule. First, we process the initial token that matched.
		if (!rulesDoNotPoll) {
			// consume and process token
			token = tokens.pollFirst();
			if (TokenType.isVariable(token.getType())) {
				// unless a namespace is explicitly specified, we use the Term subclass name as
				// a name space for the variable
				String context = nameSpace == null ? this.name : nameSpace;
				data = new Variable(context, token.getSource());
			}
			if (TokenType.isValue(token.getType())) {
				data = new Value(token.getSource());
			}
		}
		// Next, the production rule may list other terms that should be processed in
		// sequence
		// We have these terms parse their part of the token list. Subsequently we add
		// the result as a subtree
		for (Class<? extends Term> t : matchedRule) {
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
		token = tokens.pollFirst();
		if (!token.getType().equals(closingTokenType))
			throw new ParseException(closingTokenType, tokens);
		return ast;
	}

	public void analyze(AST ast) {
		if (data == null || !data.isVariable())
			return;
		// Data is a variable.
		AST parent = ast.getParent();
		if (parent != null && parent.get().variableRequiresPriorDeclaration) {
			// The parent of this term has imposed a requirement that
			// prior declaration of the variable is required.
			if (!isRegisteredVariable(ast, (Variable) data)) {
				throw new SemanticsException(ast,
						"Variable " + ((Variable) data).getName() + " must be declared prior to its use");
			}
		}
		// make sure that variable is registered
		if (!registerVariable(ast, (Variable) data)) {
			throw new SemanticsException(ast, "Please configure at least one VariableTable in the scope for this Term");
		}

		
	}

	public void evaluateEnter(AST ast, CodeGenerator code) {
		if (functionType != null) {
			function = code.createFunction(functionType);
			function.setup(code);
		}
	}

	public void evaluateExit(AST ast, CodeGenerator code) {
		if (DEBUG) {
			System.err.println("At start of evaluateExit of '" + name + "'\n" + code.getStackContent());
		}
		if (data != null && data.isValue()) {
			code.push(data);
		}
		if (data != null && data.isVariable()) {
			code.push(data);
		}

		if (functionType != null) {
			code.push(function);
			if (DEBUG) {
				System.err.println("Function pushed in evaluateExit of '" + name + "'\n" + code.getStackContent());
			}
			// popValue will apply the function AND evaluate its result, until that result
			// is a Value type result
			Value v = code.popValue();
//			if (DEBUG)
//				System.out.println("function result = " + v);
//			if (DEBUG)
//				System.out.println("stack is empty? " + code.isEmptyStack());
			if (v != null) {
				// we make the resulting value available to parent Terms
//				if (DEBUG)
//					System.out.println("AT " + name + " PUSHING FUNCTION RESULT: " + v);
				code.push(v);
			}
			if (DEBUG) {
				System.err.println("After application of function, in evaluateExit of '" + name + "'\n" + code.getStackContent());
			}
		}

	}

	// utility methods

	protected String toBNF() {
		return "<" + name + ">";
	}

	protected String toOptionalBNF() {
		if (rulesAreOptional) {
			return "[" + toBNF() + "]";
		} else {
			return toBNF();
		}
	}

	// Hack: we use the raw Class[] type to allow the caller to initialize this
	// parameter with literal data
	// e.g. Class[] cList = {Program.class};
	// Note that java does not yet seem to support such initialization for Class<?
	// extends Term>[].
	protected void addRule(TokenType tokenType, @SuppressWarnings("rawtypes") Class[] classList) {
		ArrayList<Class<? extends Term>> production = makeProduction(classList);
		rules.put(tokenType, production);
	}

	@SuppressWarnings("unchecked")
	protected static ArrayList<Class<? extends Term>> makeProduction(@SuppressWarnings("rawtypes") Class[] classList) {
		ArrayList<Class<? extends Term>> production = new ArrayList<Class<? extends Term>>();
		if (classList != null) {
			for (Class<? extends Term> c : classList) {
				production.add(c);
			}
		}
		return production;
	}

	protected void addFunction(StackableFunctionType functionType) {
		this.functionType = functionType;
	}

	protected void collectTermSubClasses(ArrayList<Class<? extends Term>> termSet) {
		Iterator<Entry<TokenType, ArrayList<Class<? extends Term>>>> it = rules.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<TokenType, ArrayList<Class<? extends Term>>> entry = (Map.Entry<TokenType, ArrayList<Class<? extends Term>>>) it
					.next();
			for (Class<? extends Term> t : entry.getValue()) {
				if (!termSet.contains(t)) {
					termSet.add(t);
					Term term = instantiateRuntimeClass(t);
					term.collectTermSubClasses(termSet);
				}
			}
		}
	}

	protected static Term instantiateRuntimeClass(Class<? extends Term> runtimeClass) {
		Constructor<? extends Term> co;
		Term term = null;
		try {
			co = runtimeClass.getConstructor();
			term = (Term) co.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return term;
	}

}
