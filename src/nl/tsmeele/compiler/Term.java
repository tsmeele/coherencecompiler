package nl.tsmeele.compiler;

import java.util.ArrayList;
import java.util.List;

public abstract class Term {
	protected final Symbol SYMBOL_BLOCK = new Symbol(NameSpace.EXECUTIONS, "BLOCK"); 
	private List<Symbol> scopedSymbolTable = null;
	
	
	public abstract AST executeParse(TokenList tokens);
	public abstract void executeAnalysis(AST ast);
	
	public void executeEvaluateEnter(AST ast, CodeGenerator code) { } // empty, can be overridden
	public void executeEvaluateExit(AST ast, CodeGenerator code) { } // empty, can be overridden
	
	
	public AST parse(Term term, TokenList tokens) {
		return term.executeParse(tokens);
	}
	

	
	public String toString() {
		return this.getClass().getSimpleName() + " " + detailInfo();
	}
	
	protected String detailInfo() {
		// Term subclasses are encouraged to override this method
		return "";
	}
	
	protected AST initTree() {
		return new AST(this);
	}
	
	
	public boolean hasSymbolScope() {
		return scopedSymbolTable != null;
	}

	public void createSymbolScope() {
		if (scopedSymbolTable == null) {
			scopedSymbolTable = new ArrayList<Symbol>();
		}
	}
	
	public boolean hasSymbol(Symbol symbol) {
		return (scopedSymbolTable != null && scopedSymbolTable.contains(symbol));
	}
	
	public boolean addSymbol(Symbol symbol) {
		return scopedSymbolTable != null && scopedSymbolTable.add(symbol);
	}
	
	public Symbol getSymbol(Symbol symbol) {
		for (Symbol s : scopedSymbolTable) {
			// we match with an object that has carries the same namespace::name
			if (s.equals(symbol)) return s;
		}
		return null;
	}
	
	public String listSymbols() {
		if (scopedSymbolTable == null) return "";
		String text = "[";
		for (int i = 0; i < scopedSymbolTable.size(); i++) {
			if (i != 0) {
				text = text.concat(", ");
			}
			text = text.concat(scopedSymbolTable.get(i).toString());
		}
		return text + "]";
		
	}
	

	
	
	
	
	
//	// clone the runtime subclass
//	public Term clone() {
//		Class<? extends Term> term = this.getClass();
//		try {
//			return term.newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	

	
	
}
