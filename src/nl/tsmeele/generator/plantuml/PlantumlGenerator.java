package nl.tsmeele.generator.plantuml;

import nl.tsmeele.compiler.AST;
import nl.tsmeele.compiler.CodeGenerator;
import nl.tsmeele.compiler.StackableFunction;
import nl.tsmeele.generator.common.FunctionAddInteger;
import nl.tsmeele.generator.common.FunctionMessage;
import nl.tsmeele.generator.common.FunctionSubtractInteger;
import nl.tsmeele.generator.common.FunctionWithThreads;
import nl.tsmeele.generator.common.StackableFunctionType;


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
	
	
	@Override
	public void beginEvaluation(AST ast) {
		System.out.println(HEADER);
	}
	
	@Override
	public void endEvaluation(AST ast) {
		System.out.println(FOOTER);
	}
	
	
	@Override
	public StackableFunction createFunction(StackableFunctionType type) {
		switch (type) {
		case INTEGER_ADDITION: return new FunctionAddInteger();
		case INTEGER_SUBTRACTION: return new FunctionSubtractInteger();
		case WITH_THREADS: return new FunctionWithThreads();
		case PROTOCOL_ROLES: return new FunctionProtocolRoles();
		case PROTOCOL: return new FunctionProtocol();
		case COMMUNICATION: return new FunctionCommunication();
		case MESSAGE: return new FunctionMessage();
		case ATOMIC_ENTER: return new FunctionAtomicEnter();
		case ATOMIC_EXIT: return new FunctionAtomicExit();
		
		case COHERENT: return new FunctionCoherent();
		}
		return null;
	}

	
	
}
