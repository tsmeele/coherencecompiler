package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.Iterator;

public class CoherentAttributes extends ArrayList<AttributeList> implements Cloneable {
	private static final long serialVersionUID = -5804798048161545146L;

	
	public Iterator<Attribute[]> iterateAttributes() {
		return new CoherentAttributesIterator(this);
	}

	@Override
	public CoherentAttributes clone() {
		CoherentAttributes copy = new CoherentAttributes();
		for (AttributeList attrSet : this) {
			AttributeList attrs = new AttributeList();
			for (Attribute attr : attrSet) {
				attrs.add(attr);
			}
			copy.add(attrs);
		}
		return copy;
	}

	
}
