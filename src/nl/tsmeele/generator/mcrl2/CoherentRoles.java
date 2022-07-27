package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.Iterator;

public class CoherentRoles extends ArrayList<ArrayList<String>>  {
	private static final long serialVersionUID = -5804798048161545146L;

	
	public Iterator<String[]> iterateRoles() {
		return new CoherentRolesIterator(this);
	}


	
}
