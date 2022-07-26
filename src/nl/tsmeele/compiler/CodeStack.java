package nl.tsmeele.compiler;

import java.util.Iterator;
import java.util.Stack;

public class CodeStack extends Stack<Stackable> {
	
	private static final long serialVersionUID = -3203470651111844379L;

	public String toString() {
		String text = "";
		int elementNo = 0;
		Iterator<Stackable> it = iterator();
		if (!it.hasNext()) {
			text = text.concat("---stack is empty---\n");
		} 
		while (it.hasNext()) {
			Stackable st = it.next();
			text = text.concat(Integer.toString(elementNo) + " (" + st.getType() + "): " + st.toString() + "\n");
			if (st.isVariable()) {
				Value stVal = ((Variable)st).getValue();
				if (stVal != null) {
					text = text.concat(" -> (" + stVal.getValueType() + "): " + stVal.toString() + "\n");
				}
			}
			elementNo++;
		}
		return text;
	}
	
	
}
