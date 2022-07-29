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
	public String[] coherentRoles = new String[2];
	public Map<String, String> protocols = new HashMap<String, String>();

	public boolean addSynchronizedTerminationAction = false;
	public CoherentRoles coherentRoleSets = new CoherentRoles();

	// variables internal to this class
	private Iterator<String[]> cohIt = null;

	private enum ProjectionType {
		OTHER, COMM, LOCK, UNLOCK
	};

	public String toString() {
		String text = "Roles: " + roles + "\n" + "Values: " + values + "\n" + "coherent roles: " + coherentRoles[0]
				+ " " + coherentRoles[1] + "\n" + printMap(protocols) + "\nCoherent sets: " + printCoherence();
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
		copy.setCoherent(coherentRoles[0], coherentRoles[1]);
		for (String protocolName : protocols.keySet()) {
			copy.protocols.put(protocolName, protocols.get(protocolName));
		}
		copy.coherentRoleSets = coherentRoleSets.clone();
		copy.addSynchronizedTerminationAction = this.addSynchronizedTerminationAction;
		return copy;
	}

	public void initializeCoherenceVariations() {
		cohIt = coherentRoleSets.iterateRoles();
	}

	public boolean renderCoherenceVariation() {
		if (!cohIt.hasNext()) {
			return false;
		}
		coherentRoles = cohIt.next();
		return true;
	}

	public void registerCoherentRoleSets(CoherentRoles coherentRoleSets) {
		this.coherentRoleSets = coherentRoleSets;
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

	public void setCoherent(String role1, String role2) {
		registerRole(role1);
		registerRole(role2);
		coherentRoles[0] = role1;
		coherentRoles[1] = role2;
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
		Pattern pComm = Pattern.compile("^C[(]([A-Za-z0-9]+)[,]([A-Za-z0-9]+)[,]([A-Za-z0-9]+)[)]");
		Pattern pLocks = Pattern.compile("^(lock|unlock)[(]([A-Za-z0-9]+),([A-Za-z0-9]+)[)]");
		while (globalDef.length() > 0) {
			// default action is to copy one character of the global protocol to the local
			// protocols
			ProjectionType pt = ProjectionType.OTHER;
			String consumed = globalDef.substring(0, 1);
			String fromRole = "";
			String toRole = "";
			String value = "";
			// if we find an operation, then we process the operation in one go
			Matcher m = pComm.matcher(globalDef);
			if (m.find()) {
				// communication operation
				pt = ProjectionType.COMM;
				consumed = m.group(0);
				fromRole = m.group(1);
				toRole = m.group(2);
				value = m.group(3);
			}
			m = pLocks.matcher(globalDef);
			if (m.find()) {
				// (un)lock operation
				pt = ProjectionType.LOCK;
				consumed = m.group(0);
				pt = consumed.equals("lock") ? ProjectionType.LOCK : ProjectionType.UNLOCK;
				fromRole = m.group(1);
				toRole = m.group(2);
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
							localDefinition + projectOperation(role, pt, fromRole, toRole, value));
					break;
				}
				}
			}
			globalDef = globalDef.substring(consumed.length());
		}
	}

	private String projectOperation(String role, ProjectionType pt, String fromRole, String toRole, String value) {
		if (role.equals(fromRole)) {
			switch (pt) {
			case COMM:
				return "send(" + fromRole + "," + toRole + "," + value + ")";
			case LOCK:
				return "lock(" + fromRole + "," + toRole + ")";
			case UNLOCK:
			default:
				return "unlock(" + fromRole + "," + toRole + ")";
			}
		}
		if (role.equals(toRole)) {
			switch (pt) {
			case COMM:
				return "receive(" + fromRole + "," + toRole + "," + value + ")";
			case LOCK:
				return "lock(" + fromRole + "," + toRole + ")";
			case UNLOCK:
			default:
				return "unlock(" + fromRole + "," + toRole + ")";
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
		for (ArrayList<String> roleSet : coherentRoleSets) {
			text = text.concat(roleSet.toString() + "\n");
		}
		return text;
	}

}
