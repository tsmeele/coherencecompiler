package nl.tsmeele.generator.mcrl2;


public class Mcrl2Role {
	private final String PREFIX = "r";
	private final String INFIX = "'";
	
	private String roleName = null;
	private String objectName = null;
	private String value = null;
	
	
	public Mcrl2Role(String roleName, String objectName) {
		this.roleName = roleName;
		this.objectName = objectName;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String s) {
		value = s;
	}
	
	public String getId() {
		return PREFIX + roleName + INFIX + objectName; 
	}
	
	@Override
	public boolean equals(Object r) {
		return 	(r.getClass() == this.getClass()) && 
				(((Mcrl2Role)r).getRoleName() == roleName) && 
				(((Mcrl2Role)r).getObjectName().equals(objectName)); 
	}

}
