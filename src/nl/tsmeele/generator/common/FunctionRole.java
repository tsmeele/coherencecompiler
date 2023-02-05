package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.grammar.Attribute;

/**
 * if an optional attribute is present then add this to the role
 * 
 * @author ton
 *
 */
public class FunctionRole extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		if (this.getStackFrameSize() <= 1) {
			// role does not have an attached attribute
			return null;
		}
		// add attribute (variable) as role value
		Variable attribute = code.popVariable();
		Variable role = code.popVariable();  
		role.setValue(new Value(attribute));
		code.push(role); // publish the enriched role
		return null; // we do not want the Variable type result to be interpreted as a value
	}

}
