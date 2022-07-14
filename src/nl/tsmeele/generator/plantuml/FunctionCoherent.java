package nl.tsmeele.generator.plantuml;

import java.util.ArrayList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;

public class FunctionCoherent extends StackableFunction {
	ArrayList<String> variables = new ArrayList<String>();

	@Override
	public Stackable apply(CodeGenerator code) {
		Variable irodsObject = code.popVariable();
		// do something with the variable
	//	System.out.println("will be coherent: " + irodsObject.getName());
		return null;
	}

}
