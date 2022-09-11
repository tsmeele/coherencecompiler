package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.List;

public class AttributeList extends ArrayList<Attribute> {

	private static final long serialVersionUID = 350403707387706370L;
	
	public List<String> getAllRoles() {
		List<String> roles = new ArrayList<String>();
		for (Attribute attr : this) {
			String r = attr.getRole();
			if (!roles.contains(r)) {
				roles.add(r);
			}
		}
		return roles;
	}
	
	
}
