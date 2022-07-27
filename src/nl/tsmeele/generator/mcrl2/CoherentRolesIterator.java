package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.Iterator;

public class CoherentRolesIterator implements Iterator<String[]> {
	private Iterator<ArrayList<String>> itSet = null;
	private Iterator<String> itRole = null;
	private ArrayList<String> currentSet = null;
	private String role0 = null;
	
	public CoherentRolesIterator(CoherentRoles coherent) {
		itSet = coherent.iterator();
		// we use pairs of roles to test coherence
		// the first role in the pair is fixed, it is the first role in a set
		// we vary the second role by iterating through the set
		// once a set is done we continue with a next set until all sets are done
	}
	
	
	@Override
	public boolean hasNext() {
		if (itRole != null && itRole.hasNext()) {
			// we are in a set and their is another 'second' role available
			return true;
		}
		// we will need to open a new set
		while (itSet != null && itSet.hasNext()) {
			// open the next set
			currentSet = itSet.next();
			itRole = currentSet.iterator();
			if (itRole.hasNext()) {
				// we already read the first role in the set 
				role0 = itRole.next();
			}
			// there needs to be at least a second role in the set, otherwise forget about this set
			if (itRole.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] next() {
		if (!hasNext()) {
			return null;
		}
		String[] roleSet = new String[2];
		roleSet[0] = role0;
		roleSet[1] = itRole.next();
		return roleSet;
	}
	

}
