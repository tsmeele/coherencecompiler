package nl.tsmeele.compiler;

public class Symbol {
	private NameSpace nameSpace;
	private String name;
	
	public Symbol(NameSpace nameSpace, String name) {
		this.nameSpace = nameSpace;
		this.name = name;
	}

	public NameSpace getNameSpace() {
		return nameSpace;
	}
	
	public String getName() {
		return name;
	}
	
	
	@Override
	public boolean equals(Object s) {
		return 	(s.getClass() == this.getClass()) && 
				(((Symbol)s).getNameSpace() == nameSpace) && 
				(((Symbol)s).getName().equals(name)); 
	}
	
	public String toString() {
		return nameSpace + "::" + name;
	}
	

	
}
