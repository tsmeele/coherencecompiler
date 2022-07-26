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

	// creates a 'template copy' of the AST tree
	// where nodes are new instances of the Terms rather then cloned copies of the Terms
	public AST createSimilar() {
		Term t = Term.instantiateRuntimeClass(get().getClass());
		AST copy = new AST(t);
		Iterator<AST> it = iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			AST childCopy = child.createSimilar();
			copy.addChild(childCopy);
		}
		return copy;
	}
	
	// semantic analysis related methods

	public void analyze() {
		analyzeSubtree(this);
	}

	private void analyzeSubtree(AST ast) {
		get().analyze(ast); 
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST subTree = it.next();
			subTree.analyzeSubtree(subTree);
		}
	}

	// evaluation related methods

	public void evaluate(CodeGenerator code) {
		code.beginEvaluation(this);
		evaluateSubtree(this, code);
		code.endEvaluation(this);
		return;
	}

	private void evaluateSubtree(AST ast, CodeGenerator code) {
		get().evaluateEnter(ast, code);
		Iterator<AST> it = ast.iterateChildren();
		while (it.hasNext()) {
			AST child = it.next();
			child.evaluateSubtree(child, code);
		}
		get().evaluateExit(ast, code);
	}

}
