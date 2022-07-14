package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

public class FunctionCoherent extends StackableFunction {
	ArrayList<String> variables = new ArrayList<String>();

	@Override
	public Stackable apply(CodeGenerator code) {
		Value count = code.popValue();
		for (int i = 0; i < count.getInteger(); i++) {
			Variable var = code.popVariable();
			variables.add(var.getName());
		}
		// do something with the variables
		((Mcrl2Generator)code).getMcrl2().addCoherentVariables(variables);
		return null;
	}

}
