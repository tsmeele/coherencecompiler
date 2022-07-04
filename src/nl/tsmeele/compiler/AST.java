package nl.tsmeele.compiler;

import java.util.Iterator;

import nl.tsmeele.structures.Tree;

public class AST extends Tree<Term> {


	public AST(Term term) {
		super(term);
	}
	
	
	@Override
	public AST getParent() {
		return (AST) super.getParent();
	}

	public Iterator<AST> iterateChildren() {
		return new ASTIterator(this);
	}
	
	// semantic analysis related methods
	
	public void analyze() {
		get().executeAnalysis(this);
		analyzeChildren(this);
	}
	

		
	private void analyzeChildren(AST ast) {
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			child.get().executeAnalysis(child);
			analyzeChildren(child);
		}
	}
	
	// evaluation related methods
	
	public void evaluate(CodeGenerator code) {
		code.beginEvaluation(this);
		get().executeEvaluateEnter(this, code);
		evaluateChildren(this, code);
		get().executeEvaluateExit(this, code);
		code.endEvaluation(this);
		return;
	}
	
	private void evaluateChildren(AST ast, CodeGenerator code) {
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			child.get().executeEvaluateEnter(child, code);
			evaluateChildren(child, code);
			child.get().executeEvaluateExit(child, code);
		}
	}
	
	
	// symbols related methods
	
	public Symbol getScopedSymbol(Symbol symbol) {
		AST ast = findSymbol(symbol);
		if (ast == null) return null;
		return ast.get().getSymbol(symbol);
	}
	
	public AST findSymbol(Symbol symbol) {
		AST node = this;
		while (node != null) {
			Term term = node.get();
			if (term.hasSymbol(symbol)) return node;
			node = node.getParent();
		}
		return null;
	}
	
	public boolean addSymbol(Symbol symbol) {
		AST node = this;
		while (node != null) {
			Term term = node.get();
			if (term.hasSymbolScope()) 
				return term.addSymbol(symbol);
			node = node.getParent();
		}
		return false;
	}
	
	public void testSymbolNotExistsAndAdd(Symbol symbol) {
		if (findSymbol(symbol) != null) 
			throw new SemanticsException(this, "Duplicate entry: " + symbol);
		addSymbol(symbol);
	}
	
	public void testSymbolExists(Symbol symbol) {
		if (findSymbol(symbol) == null) 
			throw new SemanticsException(this, "Undefined entry: " + symbol);
	}	
	
}
