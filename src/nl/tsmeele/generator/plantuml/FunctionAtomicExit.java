package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Variable;

public class FunctionAtomicExit extends StackableFunction {
	@Override
	public Stackable apply(CodeGenerator code) {
		Variable operation = code.popVariable();
		System.out.println("end");
		return null;
	}
}
