package nl.tsmeele.compiler;

import java.util.ArrayList;
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
		get().analyze(this);
		analyzeChildren(this);
	}
	

		
	private void analyzeChildren(AST ast) {
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			child.get().analyze(child);
			analyzeChildren(child);
		}
	}
	
	// evaluation related methods
	
	public void evaluate(CodeGenerator code) {
		code.beginEvaluation(this);
		get().evaluateEnter(this, code);
		evaluateChildren(this, code);
		get().evaluateExit(this, code);
		code.endEvaluation(this);
		return;
	}
	
	private void evaluateChildren(AST ast, CodeGenerator code) {
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			child.get().evaluateEnter(child, code);
			evaluateChildren(child, code);
			child.get().evaluateExit(child, code);
		}
	}
	
	

	
	
}
