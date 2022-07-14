package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.grammar.IrodsObject;

public class FunctionMessage extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		Variable message = code.popVariable();
		Stackable data = code.peek();
		if (data.isVariable() && ((Variable) data).getNameSpace().equals(IrodsObject.class.getSimpleName())) {
			// message includes an iRODS object reference, add this reference as value to the message variable
			data = code.popVariable();
			message.setValue( new Value(((Variable)data).getName()) );
			
		}
		code.push(message);
		return null;
	}

}
