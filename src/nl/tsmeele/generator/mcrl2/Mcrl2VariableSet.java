package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Mcrl2VariableSet {
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

	public String toString() {
		String text = "Roles: " + roles + "\n" + "Values: " + values + "\n" + "coherent roles: " + coherentAttributes[0]
				+ " " + coherentAttributes[1] + "\n" + printMap(protocols) + "\nCoherent sets: " + printCoherence();
		return text;
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
	
	public void optimizeProtocols() {
		Map<String, String> optimized = new HashMap<String, String>();
		for (String protocol : protocols.keySet()) {
			String opt = optimized(protocols.get(protocol));
			if (!opt.equals("tau")) {
				optimized.put(protocol, opt);
			}
		}
		protocols = optimized;
	}
	
	private String optimized(String protocolSpec) {
		String twoTau = "tau.tau";
		String oneTau = "tau";
		while (protocolSpec.contains(twoTau)) {
			protocolSpec = protocolSpec.replace(twoTau, oneTau);
		}
		if (protocolSpec.endsWith(".tau")) {
			protocolSpec = protocolSpec.substring(0, protocolSpec.length()-4);
		}
		return protocolSpec;
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
