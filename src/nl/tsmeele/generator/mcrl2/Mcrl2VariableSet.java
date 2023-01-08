package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mcrl2VariableSet implements Cloneable {
	// the names of regular roles, values and protocols should start with the
	// assigned prefix, to ensure that they occupy independent name spaces
	public static final String rolePrefix = "R";
	public static final String valuePrefix = "V";
	public static final String protocolPrefix = "P";

	// reserved special values and roles (name space must not overlap with regular
	// values and roles)
	// do not add any of the special values/roles to the below Lists. They will be
	// added to a program by the Mcrl2Template class
	public static final String initialValue = "zero"; // all properties of all roles will initially carry this value
	public static final String dormantRole = "b"; // all roles will initially reference this role as actor for "property
													// most recently changed by"

	// the set of variables, consider to use the "register" methods to add members
	public List<String> values = new ArrayList<String>();
	public List<String> roles = new ArrayList<String>();
	public Attribute[] coherentAttributes = new Attribute[2];
	public Map<String, String> protocols = new HashMap<String, String>();

	public boolean addSynchronizedTerminationAction = false;
	public CoherentAttributes coherentAttrSets = new CoherentAttributes();

	// variables internal to this class
	private Iterator<Attribute[]> cohIt = null;

	// projection global -> local protocols
	private enum OperationType {
		OTHER, COMM, LOCK, UNLOCK
	};

	public String toString() {
		String text = "Roles: " + roles + "\n" + "Values: " + values + "\n" + "coherent roles: " + coherentAttributes[0]
				+ " " + coherentAttributes[1] + "\n" + printMap(protocols) + "\nCoherent sets: " + printCoherence();
		return text;
	}

	public Mcrl2VariableSet clone() {
		Mcrl2VariableSet copy = new Mcrl2VariableSet();
		for (String value : values) {
			copy.registerValue(value);
		}
		for (String role : roles) {
			copy.registerRole(role);
		}
		copy.setCoherent(coherentAttributes[0], coherentAttributes[1]);
		for (String protocolName : protocols.keySet()) {
			copy.protocols.put(protocolName, protocols.get(protocolName));
		}
		copy.coherentAttrSets = coherentAttrSets.clone();
		copy.addSynchronizedTerminationAction = this.addSynchronizedTerminationAction;
		return copy;
	}

	public void initializeCoherenceVariations() {
		cohIt = coherentAttrSets.iterateAttributes();
	}

	public boolean renderCoherenceVariation() {
		if (!cohIt.hasNext()) {
			return false;
		}
		coherentAttributes = cohIt.next();
		return true;
	}

	public void registerCoherentRoleSets(CoherentAttributes coherentAttrSets) {
		this.coherentAttrSets = coherentAttrSets;
	}

	public void registerProtocol(String protocolName, String definition) {
		if (!protocols.containsKey(protocolName)) {
			protocols.put(protocolName, definition);
		}
	}

	public void registerValue(String value) {
		if (!values.contains(value)) {
			values.add(value);
		}
	}

	public void registerRole(String role) {
		if (!roles.contains(role)) {
			roles.add(role);
		}
	}

	public void setCoherent(Attribute attr1, Attribute attr2) {
		registerRole(attr1.getRole());
		registerRole(attr2.getRole());
		coherentAttributes[0] = attr1;
		coherentAttributes[1] = attr2;
	}

	public Mcrl2VariableSet project2localprotocols() {
		Mcrl2VariableSet local = this.clone();
		local.protocols = new HashMap<String, String>();
		for (String globalProtocol : protocols.keySet()) {
			projectOneProtocol2Local(local, globalProtocol, protocols.get(globalProtocol));
		}
		return local;
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
		while (globalDef.length() > 0) {
			// default action is to copy one character of the global protocol to the local
			// protocols
			OperationType pt = OperationType.OTHER;
			String consumed = globalDef.substring(0, 1);
			String fromRole = "";
			String toRole = "";
			String toAttr = "";
			String value = "";
			// if we find an operation, then we process the operation in one go
			Matcher m = pComm.matcher(globalDef);
			if (m.find()) {
				// communication operation
				pt = OperationType.COMM;
				consumed = m.group(0);
				fromRole = m.group(1);
				toRole = m.group(2);
				toAttr = m.group(3);
				value = m.group(4);
			}
			m = pLocks.matcher(globalDef);
			if (m.find()) {
				// (un)lock operation
				consumed = m.group(0);
				pt = consumed.startsWith("lock") ? OperationType.LOCK : OperationType.UNLOCK;
				fromRole = m.group(2);
				toRole = m.group(3);
				toAttr = m.group(4);
				value = null;
			}
			// process the global protocol piece to the local protocols
			for (String role : local.roles) {
				String localName = globalName + role;
				String localDefinition = local.protocols.get(localName) == null ? "" : local.protocols.get(localName);
				switch (pt) {
				case OTHER: {
					local.protocols.put(localName, localDefinition + consumed);
					break;
				}
				case COMM:
				case LOCK:
				case UNLOCK: {
					local.protocols.put(localName,
							localDefinition + projectOperation(role, pt, fromRole, toRole, toAttr, value));
					break;
				}
				}
			}
			globalDef = globalDef.substring(consumed.length());
		}
	}

	private String projectOperation(String role, OperationType pt, String fromRole, 
			String toRole, String toAttr, String value) {
		if (role.equals(fromRole)) {
			switch (pt) {
			case COMM:
				return "send(" + fromRole + "," + toRole + "," + toAttr + "," + value + ")";
			case LOCK:
				return "send(" + fromRole + "," + toRole + "," + toAttr + "," + "Lock" + ")" +
			           ".receive(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case UNLOCK:
				return "send(" + fromRole + "," + toRole + "," + toAttr + "," + "Unlock" + ")" +
		           ".receive(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			default:
				return "tau";
			}
		}
		if (role.equals(toRole)) {
			switch (pt) {
			case COMM:
				return "receive(" + fromRole + "," + toRole + "," + toAttr + "," + value + ")";
			case LOCK:
				return "receive(" + fromRole + "," + toRole + "," + toAttr + "," + "Lock" + ")" +
			           ".send(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			case UNLOCK:
				return "receive(" + fromRole + "," + toRole + "," + toAttr + "," + "Unlock" + ")" +
		           ".send(" + toRole + "," + fromRole + "," + "0" + "," + "Ack" + ")";
			default:
				return "tau";
			}
		}
		// other role
		return "tau";
	}

	private String printMap(Map<String, String> map) {
		String text = "";
		for (String key : map.keySet()) {
			text = text.concat("  " + key + " -> " + map.get(key) + "\n");
		}
		return text;
	}

	private String printCoherence() {
		String text = "";
		for (ArrayList<Attribute> attrSet : coherentAttrSets) {
			text = text.concat(attrSet.toString() + "\n");
		}
		return text;
	}

}
