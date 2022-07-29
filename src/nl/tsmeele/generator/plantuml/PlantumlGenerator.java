package nl.tsmeele.generator.plantuml;

import java.io.PrintStream;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.GeneratorException;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.compiler.TargetCode;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.generator.common.FunctionAddInteger;
import nl.tsmeele.generator.common.FunctionAtomicStatement;
import nl.tsmeele.generator.common.FunctionParticipants;
import nl.tsmeele.generator.common.FunctionRole;
import nl.tsmeele.generator.common.FunctionStatementBlock;
import nl.tsmeele.generator.common.FunctionSubtractInteger;
import nl.tsmeele.generator.common.FunctionWithThreads;
import nl.tsmeele.generator.common.StringTargetCode;
import nl.tsmeele.grammar.StackableFunctionType;


/**
 * This class is a code generator that produces source text for a PlantUML sequence diagram.
 * We seek to use a diagram style that conforms to iRODS style guidelines. 
 * 
 * For more information on PlantUML, see http://plantuml.com 
 * 
 * @author Ton Smeele
 *
 */
public class PlantumlGenerator extends CodeGenerator {

	private final String HEADER = "@startuml\n" + 
			"skinparam responseMessageBelowArrow true \n" + 
			"\n" + 
			"skinparam sequence {\n" + 
			"ArrowColor DarkCyan\n" + 
			"\n" + 
			"LifeLineBorderColor Teal\n" + 
			"LifeLineBackgroundColor TECHNOLOGY\n" + 
			"\n" + 
			"ParticipantBorderColor Teal\n" + 
			"ParticipantBackgroundColor TECHNOLOGY\n" + 
			"ParticipantFontColor Black\n" + 
			"}";
	private final String FOOTER = "@enduml\n";
	
	public PlantumlGenerator(PrintStream out) {
		super(out);
	}
	
	@Override
	public void beginEvaluation(AST ast) {
		out.println(HEADER);
	}
	
	@Override
	public void endEvaluation(AST ast) {
		Value result = (Value) popValue();
		if (!result.isCode()) {
			throw new GeneratorException("Expected target code at end evaluation, found " + result.getValueType());
		}
		for (TargetCode tc : result.getCode()) {
			if (tc.renderAsString() == null) {
				// filter empty code fragments
				continue;
			}
			out.println(tc.renderAsString());
		}
		out.println(FOOTER);
	}
	
	
	@Override
	public StackableFunction createFunction(StackableFunctionType type) {
		switch (type) {
		// common functions
		case INTEGER_ADDITION: return new FunctionAddInteger();
		case INTEGER_SUBTRACTION: return new FunctionSubtractInteger();
		case WITH_THREADS: return new FunctionWithThreads();
		case ATOMIC: return new FunctionAtomicStatement();
		case ROLE: return new FunctionRole();
		case STATEMENT_BLOCK: return new FunctionStatementBlock();

		// PlantUML specific functions
		case PROTOCOL: return new FunctionProtocol();
		case COMMUNICATION: return new FunctionCommunication();
		case ATOMIC_BLOCK: return new FunctionAtomicBlock();
		case COHERENT: return new FunctionCoherent();
		case PARTICIPANTS: return new FunctionParticipants();
		case IFTHENELSE: return new FunctionIfThenElse();
		}
		return null;
	}

	@Override
	public TargetCode createNoCode() {
		return new StringTargetCode(null);
	}

	
	
}
