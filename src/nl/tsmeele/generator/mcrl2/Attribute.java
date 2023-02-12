package nl.tsmeele.generator.mcrl2;


public class Attribute implements Cloneable {
	private String role;
	private int attr;
	
	public Attribute(String role, int attr) {
		this.role = role;
		this.attr = attr;
	}
	
	@Override
	public Attribute clone() {
		return new Attribute(role, attr);
	}
	
	public String toString() {
		return role + "." + attr;
	}
	
	public String getRole() {
		return role;
	}
	
	public String getAttr() {
		return Integer.toString(attr);
	}
	
	public int getAttrNo() {
		return attr;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return getClass() == obj.getClass() &&
				role.equals( ((Attribute)obj).role) && 
				attr == ((Attribute)obj).attr;
	}


	
}
