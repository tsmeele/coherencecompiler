package nl.tsmeele.generator.mcrl2;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mcrl2VariablesLocalized extends Mcrl2VariableSet {

	// projection global -> local protocols
	private enum OperationType {
		OTHER, COMM, LOCK, UNLOCK, TEST
	};
	
	public Mcrl2VariablesLocalized(Mcrl2VariableSet global ) {
		// clone all information except protocols from global variable set
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
		
		// create local protocols from projected global protocols
		this.protocols = new HashMap<String, String>();
		for (String globalProtocol : global.protocols.keySet()) {
			projectOneProtocol2Local(this, globalProtocol, global.protocols.get(globalProtocol));
		}
		
	}
	
	private void projectOneProtocol2Local(Mcrl2VariableSet local, String globalName, String globalDef) {
		// patterns to match the operations
		// TODO: cater for attribute ref in operations
		Pattern pComm = Pattern.compile("^C[(]" +
				"([A-Za-z0-9]+)[,]" +	// g1 = from role
				"([A-Za-z0-9]+)[,]" +	// g2 = to role
				"([0-9]+)[,]" +			// g3 = to attribute
				"([A-Za-z0-9]+)[)]");	// g4 = value
		Pattern pLocks = Pattern.compile("^(lock|unlock)[(]" +	// g1 = operation
				"([A-Za-z0-9]+)[,]" +	// g2 = from role (requester)
				"([A-Za-z0-9]+)[,]" +	// g3 = to role
				"([0-9]+)[)]");			// g4 = to attribute
		Pattern pTest = Pattern.compile("^test[(]" +
				"([A-Z]+)[,]" +			// g1 = condition e.g. EQ,NEQ
				"([A-Za-z0-9]+)[,]" +	// g2 = role
				"([0-9]+)[,]" +			// g3 = attribute1
				"([0-9]+)[)]");			// g4 = attribute2
		while (globalDef.length() > 0) {
			// default action is to copy one character of the global protocol to the local
			// protocols
			Map<String,String> parms = new HashMap<String,String>();
			OperationType pt = OperationType.OTHER;
			String fromRole = "";
			String toRole = "";
			String consumed = globalDef.substring(0, 1);

			// if we find an operation, then we process the operation in one go
			Matcher m = pComm.matcher(globalDef);
			if (m.find()) {
				// communication operation
				pt = OperationType.COMM;
				consumed = m.group(0);
				fromRole = m.group(1);
				toRole = m.group(2);
				parms.put("toAttr", m.group(3));
				parms.put("value", m.group(4));
			}
			m = pLocks.matcher(globalDef);
			if (m.find()) {
				// (un)lock operation
				consumed = m.group(0);
				pt = consumed.startsWith("lock") ? OperationType.LOCK : OperationType.UNLOCK;
				fromRole = m.group(2);
				toRole = m.group(3);
				parms.put("toAttr", m.group(4));
			}
			m = pTest.matcher(globalDef);
			if (m.find()) {
				consumed = m.group(0);
				pt = OperationType.TEST;
				parms.put("condition", m.group(1));
				fromRole = m.group(2);
				parms.put("attr1", m.group(3));
				parms.put("attr2", m.group(4));
			}
			// process the global protocol piece to the local protocols
			for (String role : local.roles) {
				String localName = globalName + role;
				// retrieve whatever is already registered as local protocol, we will update its definition
				String localDefinition = local.protocols.get(localName) == null ? "" : local.protocols.get(localName);
				switch (pt) {
				case OTHER: {
					local.protocols.put(localName, localDefinition + consumed);
					break;
				}
				case COMM:
				case LOCK:
				case UNLOCK:
				case TEST: {
					local.protocols.put(localName,
							localDefinition + projectOperation(role, pt, fromRole, toRole, parms));
					break;
				}
				//TODO: add case TEST
				}
			}
			globalDef = globalDef.substring(consumed.length());
		}
	}

	private String projectOperation(String role, OperationType pt, 
			String fromRole, String toRole, Map<String,String> parms) {
		if (role.equals(fromRole)) {
			switch (pt) {
			case COMM:
				return "send(" + fromRole + "," + toRole + "," + 
					parms.get("toAttr") + "," + parms.get("value") + ")";
			case LOCK:
				return "send(" + fromRole + "," + toRole + "," + parms.get("toAttr") + "," + "Lock" + ")" +
			           ".receive(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case UNLOCK:
				return "send(" + fromRole + "," + toRole + "," + parms.get("toAttr") + "," + "Unlock" + ")" +
		           ".receive(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case TEST:
				return "test(" + parms.get("condition") + "," + fromRole + "," +
						parms.get("attr1") + "," + parms.get("attr2") + ")";
			default:
				return "tau";
			}
		}
		if (role.equals(toRole)) {
			switch (pt) {
			case COMM:
				return "receive(" + fromRole + "," + toRole + "," + 
					parms.get("toAttr") + "," + parms.get("value") + ")";
			case LOCK:
				return "receive(" + fromRole + "," + toRole + "," + parms.get("toAttr") + "," + "Lock" + ")" +
			           ".send(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case UNLOCK:
				return "receive(" + fromRole + "," + toRole + "," + parms.get("toAttr") + "," + "Unlock" + ")" +
		           ".send(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case TEST:
			default:
				return "tau";
			}
		}
		// other role
		return "tau";
	}
	
	
	
}
