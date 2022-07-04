package nl.tsmeele.generator.mcrl2;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;

public class FunctionAtomicEnter extends StackableFunction {
	@Override
	public Stackable apply(CodeGenerator code) {
		Variable operation = code.popVariable();
		System.out.println("GENERATOR:\n" + "Atomic start " + operation);
		return null;
	}
}
