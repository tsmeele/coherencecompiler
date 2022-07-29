package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.Iterator;

public class CoherentRoles extends ArrayList<ArrayList<String>> implements Cloneable {
	private static final long serialVersionUID = -5804798048161545146L;

	
	public Iterator<String[]> iterateRoles() {
		return new CoherentRolesIterator(this);
	}

	@Override
	public CoherentRoles clone() {
		CoherentRoles copy = new CoherentRoles();
		for (ArrayList<String> roleSet : this) {
			ArrayList<String> roles = new ArrayList<String>();
			for (String role : roleSet) {
				roles.add(role);
			}
			copy.add(roles);
		}
		return copy;
	}

	
}
