package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.Stackable;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.TargetCode;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;
import nl.tsmeele.generator.common.StringTargetCode;

public class FunctionAtomicBlock extends StackableFunction {
	private int stackSizeBeforeAtomicBlock = 0;
	
	@Override
	public void setup(CodeGenerator code) {
		super.setup(code);
		// we need to understand more about our context
		if (code.getStackSize() < 3) {
			throw new GeneratorException("Atomic statement syntax configuration error");
		}
		Variable protectsRole = code.popVariable();
		Variable fromRole = code.popVariable();
		Variable operation = code.popVariable();
		// restore the context to its original state
		code.push(operation);
		code.push(fromRole);
		code.push(protectsRole);
		// produce lock operation and add it to the stack
		String roleAndAttribute = protectsRole.getName();
		if (protectsRole.getValue() != null && protectsRole.getValue().isVariable()) {
			roleAndAttribute = roleAndAttribute.concat("(" + protectsRole.getValue().getVariable().getName() + ")");
		}
		TargetCode atomicLock = new StringTargetCode("group " + operation.getName() + " [critical section " + roleAndAttribute + " ]");
		code.push(new Value(atomicLock));
		stackSizeBeforeAtomicBlock = code.getStackSize();
	}
	
	
	@Override
	public Stackable apply(CodeGenerator code) {
		// read results from atomic
		Value atomicBlockResult = null;
		if (code.getStackSize() > stackSizeBeforeAtomicBlock) {
			atomicBlockResult = (Value) code.popValue();
		}
		Value atomicLock = (Value) code.popValue();
		// also consume our context (for potentiate use in the production of unlock code)
		Variable protectsRole = code.popVariable();
		Variable fromRole = code.popVariable();
		Variable operation = code.popVariable();
		// restore the context to its state prior to atomic (while consuming what happened in atomic)
		code.push(operation);
		code.push(fromRole);
		code.push(protectsRole);
		// produce unlock operation 
		TargetCode atomicUnlock = new StringTargetCode("end "); 
		// combine all code fragments produced in atomic statement, we use the atomicLock as a collector
		if (atomicBlockResult != null) {
			atomicLock.addLast(atomicBlockResult.getCode());
		}
		atomicLock.addLast(atomicUnlock);
		// publish the resulting target code 
		return atomicLock;
	}
}
