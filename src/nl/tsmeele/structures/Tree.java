package nl.tsmeele.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


public class Tree<T> implements Iterable<Tree<T>>, Cloneable {
	private final int INDENT = 2;
	private T data;
	private Tree<T> parent = null;
	private List<Tree<T>> children = new ArrayList<Tree<T>>();
	
	public Tree(T data) {
		this.data = data;
	}
	
	public String toString() {
		return toStringTree(0);
	}
	
	public T get() {
		return data;
	}
	
	public List<Tree<T>> hierarchy() {
		List<Tree<T>> hierarchy;
		if (parent == null) {
			hierarchy = new ArrayList<Tree<T>>();
		}
		else {
			hierarchy = parent.hierarchy();
		}
		hierarchy.add(this);
		return hierarchy;
	}
	
	public Tree<T> getParent() {
		return parent;
	}
	
	public int countChildren() {
		return children.size();
	}
	
	@Override
	public ListIterator<Tree<T>> iterator() {
		return children.listIterator();
	}
	
	public boolean addChild(Tree<T> child) {
		child.parent = this;
		return children.add(child); 
	}
	
	public boolean removeChild(Tree<T> child) {
		child.parent = null;
		return children.remove(child);
	}	
	
	private String toStringTree(int indent) {
		String text = blanks(indent) + get().toString() + "\n";
		ListIterator<Tree<T>> it = iterator();
		while (it.hasNext()) {
			Tree<T> child = it.next();
			text = text.concat(child.toStringTree(indent + INDENT));
		}
		return text;
	}
	
	private String blanks(int i) {
		char[] cc = new char[i];
		Arrays.fill(cc, ' ');
		return new String(cc);
	}
	
}
