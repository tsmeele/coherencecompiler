package nl.tsmeele.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nl.tsmeele.generator.common.FunctionAddInteger;
import nl.tsmeele.generator.common.StackableFunctionType;
import nl.tsmeele.grammar.TokenType;


public abstract class Term {
	final static boolean DEBUG = false;
	protected String name = getClass().getSimpleName();
	protected HashMap<TokenType, ArrayList<Class<? extends Term>>> rules = new HashMap<TokenType, ArrayList<Class<? extends Term>>>();
	
	// properties that can be overriden by subclasses upon construction:
	protected boolean rulesDoNotPoll = false;
	protected boolean rulesAreOptional = false;
	protected boolean variableRequiresPriorDeclaration = false;	
	protected TokenType closingTokenType = null;
	protected String nameSpace = null;

	// general properties
	private ArrayList<Variable> variableTable = null;	
	private Stackable data = null;
	private StackableFunctionType functionType = null;
	
	public Term() {
		//this.name = this.getClass().getSimpleName();
	}
	
	/**
	 * In its local initialize method, a Term subclass must specify at least 1 production rule 
	 * appropriate to the function of the subclass. Such rule should be added to the "rules" variable listed above.
	 * Production rules specify a token type as a pattern to match and process. It may specify additional Terms that
	 * must follow the token and need to be parsed as well (in sequence). 
	 * 
	 * Other actions that could be taken in initialize:
	 * 1. create a (scoped) symbol table.  Any variables added in the subtree will be registered in the symbol table.
	 * 2. set 'requiresPriorDeclaration' to true. 
	 *    Impact:  the variable will not be registered in a symbol table during parse phase
	 *             (because this is expected to have taken place while parsing some other Term
	 *             the variable must exist in a symbol table during the semantic analysis phase).
	 * 3. set 'rulesDoNotPOll' to true will cause the token not to be consumed, only to be inspected. The matched rule
	 *             will still fire.  This setting can be used to implement a Term that performs a switch/case.
	 * 4. set 'rulesAreOptional' to true will cause parse to succeed even if no rules match. 
	 */
	
	public String toString() {
		String dataInfo = data == null ? "" : " '" + data.toString() + "'"; 
		String tableInfo = variableTable == null ? "" : " " + variableTable;
		return name + dataInfo + tableInfo;
	}

	public String getName() {
		return name;
	}
	
	// returns a BNF style syntax for the subset of the language starting at this term
	public String toAllSyntax() {
		ArrayList<Class<? extends Term>> members = new ArrayList<Class<? extends Term>>();
		collectTermSubClasses(members);
		String syntaxText = toSyntax() + "\n";
		for (Class<? extends Term> c : members) {		
			Term term = instantiateRuntimeClass( c);
			syntaxText = syntaxText.concat(term.toSyntax() + "\n");
		}
		return syntaxText;
	}
	
	// returns a BNF style syntax for this Term
	public String toSyntax() {
		String text = toBNF() + " ::=" ;
		Iterator<TokenType> it = rules.keySet().iterator();
		while (it.hasNext()) {
			TokenType tt = it.next();
			ArrayList<Class<? extends Term>> terms = rules.get(tt);
			if (!rulesDoNotPoll) {
				text = text.concat(" " + tt );
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
			text = text.concat(" " + closingTokenType );
		}
		return text;
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
			if (term.hasVariable(v)) return true;
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
			if (rulesAreOptional) return ast;
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
				// unless a namespace is explicitly specified, we use the Term subclass name as a name space for the variable
				String context = nameSpace == null ? this.name : nameSpace;
				data = new Variable(context, token.getSource());
			}
			if (TokenType.isValue(token.getType())) {
				data = new Value(token.getSource());
			}
		}
		// Next, the production rule may list other terms that should be processed in sequence
		// We have these terms parse their part of the token list. Subsequently we add the result as a subtree 
		for (Class<? extends Term> t : matchedRule) {
			// for the subtree we need a fresh, similar type, Term instance, so that we can populate it with data
			Term term = instantiateRuntimeClass(t);
			ast.addChild(term.parse(tokens));
		}
		
		// finally we check the existence of a constant-type closing symbol
		// if present, this symbol is consumed. 
		if (closingTokenType == null) return ast;
		if (closingTokenType != null && (TokenType.isValue(closingTokenType) || TokenType.isVariable(closingTokenType)) )
			// closing token should be a constant, e.g. a keyword or a ')' symbol 
			throw new ParseException("ClosingTokenType must be constant-type token");
		token = tokens.pollFirst();
		if (!token.getType().equals(closingTokenType))
			throw new ParseException(closingTokenType, tokens);
		return ast;
	}
	
	
	public void analyze(AST ast) {
		if (data == null || !data.isVariable()) return;
		// Data is a variable. 
		AST parent = ast.getParent();
		if (parent != null && parent.get().variableRequiresPriorDeclaration) {
			// The parent of this term has imposed a requirement that
			// prior declaration of the variable is required.
			if (!isRegisteredVariable(ast, (Variable)data) ) {
				throw new SemanticsException(ast, "Variable " + ((Variable)data).getName() + " must be declared prior to its use");
			}
		}
		// make sure that variable is registered
		if (!registerVariable(ast, (Variable)data)) {
			throw new SemanticsException(ast, "Please configure at least one VariableTable in the scope for this Term");
		}

	}
	
	public void evaluateEnter(AST ast, CodeGenerator code) {
		
	}
	
	public void evaluateExit(AST ast, CodeGenerator code) {
		if (data != null && data.isValue()) {
			if (DEBUG) System.out.println("AT " + name + " PUSHING VALUE: " + data);
			code.push(data);
		}
		if (data != null && data.isVariable()) {
			if (DEBUG) System.out.println("AT " + name + " PUSHING VARIABLE: " + data);
			code.push(data);
		}
		
		
		if (functionType != null) {
			Stackable function = code.createFunction(functionType);
			if (DEBUG) System.out.println("AT " + name + " PUSHING FUNCTION: " + function);
			code.push(function);
			Value v = code.popValue();  // applies the function
			if (DEBUG) System.out.println("function result = " + v);
			if (DEBUG) System.out.println("stack is empty? " + code.isEmptyStack());
			if (v != null) {
				if (DEBUG) System.out.println("AT " + name + " PUSHING RESULT: " + v);
				code.push(v);
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
	
	// Hack: we use the raw Class[] type to allow the caller to initialize this parameter with literal data
	//       e.g. Class[] cList = {Program.class};
	//       Note that java does not yet seem to support such initialization for Class<? extends Term>[].
	protected void addRule(TokenType tokenType, @SuppressWarnings("rawtypes") Class[] classList) {
		ArrayList<Class<? extends Term>> production = makeProduction(classList);
		rules.put(tokenType, production);
	}
	
	@SuppressWarnings("unchecked")
	protected static ArrayList<Class<? extends Term>>makeProduction(@SuppressWarnings("rawtypes") Class[] classList) {
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
			Map.Entry<TokenType,ArrayList<Class<? extends Term>>> entry = (Map.Entry<TokenType,ArrayList<Class<? extends Term>>>)it.next();
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
		} catch (NoSuchMethodException | SecurityException | 
				InstantiationException | IllegalAccessException | 
				IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return term;
	}
	
	
	
	
}
