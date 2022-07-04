package nl.tsmeele.compiler;

import java.util.Iterator;
import nl.tsmeele.structures.Tree;

public class ASTIterator implements Iterator<AST> {
	private Iterator<Tree<Term>> it = null;

	
	public ASTIterator(AST ast) {
		this.it = ast.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public AST next() {
		return (AST) it.next();
	}
	
	
}
