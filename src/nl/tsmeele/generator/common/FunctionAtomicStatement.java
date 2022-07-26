package nl.tsmeele.generator.common;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.Value;

public class FunctionAtomicStatement extends StackableFunction {

	@Override
	public Stackable apply(CodeGenerator code) {
		// we clean up the stack, all data introduced by atomic statement, except for the atomic block result
		Value atomicBlockResult = (Value) code.popValue();
		code.popVariable(); // ProtectsRole
		code.popVariable(); // FromRole
		code.popVariable(); // IrodsOperation
		code.push(atomicBlockResult);
		return null;
	}

}
