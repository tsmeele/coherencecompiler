package nl.tsmeele.generator.mcrl2;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mcrl2ProjectedVariableSet extends Mcrl2VariableSet {

	private Pattern pComm = Pattern.compile("^C[(]" +
			"([A-Za-z0-9]+)[,]" +	// g1 = from role
			"([A-Za-z0-9]+)[,]" +	// g2 = to role
			"([0-9]+)[,]" +			// g3 = to attribute
			"([A-Za-z0-9]+)[)]");	// g4 = value
	
	private Pattern pLocks = Pattern.compile("^(lock|unlock)[(]" +	// g1 = operation
			"([A-Za-z0-9]+)[,]" +	// g2 = from role (requester)
			"([A-Za-z0-9]+)[,]" +	// g3 = to role
			"([0-9]+)[)]");			// g4 = to attribute
	
	private Pattern pTest = Pattern.compile("^test[(]" +
			"([A-Z]+)[,]" +			// g1 = condition e.g. EQ,NEQ
			"([A-Za-z0-9]+)[,]" +	// g2 = role
			"([0-9]+)[,]" +			// g3 = attribute1
			"([0-9]+)[)][.]");			// g4 = attribute2
	
	private class ParseResult {
		public String consumed, result;	
		public ParseResult(String consumed, String result) {
			this.consumed = consumed;
			this.result = result;
		}
	}
	
	public Mcrl2ProjectedVariableSet(Mcrl2VariableSet global ) {
		// first, clone all information except protocols from global variable set
		for (String value : global.values) {
			this.registerValue(value);
		}
		for (String role : global.roles) {
			this.registerRole(role);
		}
		coherentAttributes[0] = global.coherentAttributes[0].clone();
		coherentAttributes[1] = global.coherentAttributes[1].clone();
		setCoherent(coherentAttributes[0], coherentAttributes[1]);
		coherentAttrSets = global.coherentAttrSets.clone();
		addSynchronizedTerminationAction = global.addSynchronizedTerminationAction;		
		
		// next, build local protocols from the original global protocols
		this.protocols = new HashMap<String, String>();
		
		// multiple global protocols can run interleaved, projection is done per protocol
		for (String globalProtocolName : global.protocols.keySet()) {
			String globalProtocolDefinition = global.protocols.get(globalProtocolName);
			// project this global protocol to each of the roles
			for (String role : roles) {
				String localName = globalProtocolName + role;
				protocols.put(localName, projectProtocolToRole(globalProtocolDefinition, role));
			}
		}
		
	}
	
	private String projectProtocolToRole(String globalProtocol, String role) {
		String localProtocol = "";
		while (globalProtocol.length() > 0) {
			// parse and project a chunk of the protocol
			ParseResult parsed = nextProtocolPart(globalProtocol, role);
			// account for the projected chunk
			localProtocol = localProtocol.concat(parsed.result);
			globalProtocol = globalProtocol.substring(parsed.consumed.length());
		}
		return localProtocol;
	}
	
	private ParseResult nextProtocolPart(String globalProtocol, String role) {
		// try to find a matching mcrl2 action, if so process it 
		Matcher m = pComm.matcher(globalProtocol);
		if (m.find()) return parseCommunication(m, role);
		
		m = pLocks.matcher(globalProtocol);
		if (m.find()) return parseLock(m,role);
		
		m = pTest.matcher(globalProtocol);
		if (m.find()) return parseTest(m, role);
		
		// none of our mcrl2 actions seemed to match, just process/copy 1 character
		// e.g. this could be an mcrl2 operator "." or "+" etc
		String one = globalProtocol.substring(0,1);
		return new ParseResult(one, one);
	}
	
	private ParseResult parseCommunication(Matcher m, String role) {
		String fromRole = m.group(1);
		String toRole = m.group(2);
		String toAttr = m.group(3);
		String value = m.group(4);
		if (role.equals(fromRole)) {
			return new ParseResult(m.group(0), "send(" + fromRole + "," + 
					toRole + "," + toAttr + "," + value + ")");
		}
		if (role.equals(toRole)) {
			return new ParseResult(m.group(0), "receive(" + fromRole + "," + 
					toRole + "," + toAttr + "," + value + ")");
		}
		return new ParseResult(m.group(0), "tau");
	}
	
	private ParseResult parseLock(Matcher m, String role) {  // also parses unlock
		String operation = m.group(1); // "lock" or "unlock"
		String fromRole = m.group(2);
		String toRole = m.group(3);
		String toAttr = m.group(4);
		if (role.equals(fromRole)) {
			return new ParseResult(m.group(0), "send(" + fromRole + "," + 
					toRole + "," + toAttr + "," + operation + ")" +
					".receive(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")");
		}
		if (role.equals(toRole)) {
			return new ParseResult(m.group(0), "receive(" + fromRole + "," + 
					toRole + "," + toAttr + "," + operation + ")" +
					".send(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")");
		}
		return new ParseResult(m.group(0), "tau");
	}
	
	private ParseResult parseTest(Matcher m, String role) {		
		String condition = m.group(1);
		String testedRole = m.group(2);
		String attr1 = m.group(3);
		String attr2 = m.group(4);
		if (role.equals(testedRole)) {
			return new ParseResult(m.group(0), "test(" + condition + "," + 
					testedRole + "," + attr1 + "," + attr2 + ").");
		}
		return new ParseResult(m.group(0), "");
	}
	
	
}
