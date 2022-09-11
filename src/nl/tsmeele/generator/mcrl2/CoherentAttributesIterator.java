package nl.tsmeele.generator.mcrl2;

import java.util.Iterator;

public class CoherentAttributesIterator implements Iterator<Attribute[]> {
	private Iterator<AttributeList> itSet = null;
	private Iterator<Attribute> itAttr = null;
	private AttributeList currentSet = null;
	private Attribute attr0 = null;
	
	public CoherentAttributesIterator(CoherentAttributes coherent) {
		itSet = coherent.iterator();
		// we use pairs of attributes to test coherence
		// the first attribute in the pair is fixed, it is the first attribute in a set
		// we vary the second attribute by iterating through the set
		// once a set is done we continue with a next set until all sets are done
	}
	
	
	@Override
	public boolean hasNext() {
		if (itAttr != null && itAttr.hasNext()) {
			// we are in a set and their is another 'second' role available
			return true;
		}
		// we will need to open a new set
		while (itSet != null && itSet.hasNext()) {
			// open the next set
			currentSet = itSet.next();
			itAttr = currentSet.iterator();
			if (itAttr.hasNext()) {
				// we already read the first role in the set 
				attr0 = itAttr.next();
			}
			// there needs to be at least a second role in the set, otherwise forget about this set
			if (itAttr.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Attribute[] next() {
		if (!hasNext()) {
			return null;
		}
		Attribute[] attrSet = new Attribute[2];
		attrSet[0] = attr0;
		attrSet[1] = itAttr.next();
		return attrSet;
	}
	

}
