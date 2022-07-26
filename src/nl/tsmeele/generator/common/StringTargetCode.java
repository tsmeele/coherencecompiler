package nl.tsmeele.generator.common;

import java.util.LinkedList;

import nl.tsmeele.compiler.TargetCode;

public class StringTargetCode implements TargetCode {
	private String code = null;
	
	public StringTargetCode(String code) {
		this.code = code;
	}
	
	@Override
	public String renderAsString() {
		return code;
	}


	
	

}
