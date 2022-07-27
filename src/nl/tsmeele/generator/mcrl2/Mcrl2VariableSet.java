package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mcrl2VariableSet {
	// the names of regular roles, values and protocols should start with the assigned prefix, to ensure that they occupy independent name spaces
	public static final String rolePrefix = "R";
	public static final String valuePrefix = "V";
	public static final String protocolPrefix = "P";

	// special values and roles (name space must not overlap with regular values and roles)
	public static final String initialValue = "zero"; 	// all properties of all roles will initially carry this value
	public static final String dormantRole = "b"; 		// all roles will initially reference this role as actor for "property most recently changed by"

	// the set of variables, use the "register" methods to add members
	public List<String> values = new ArrayList<String>();
	public List<String> roles = new ArrayList<String>();
	public String[] coherentRoles = new String[2];				
	public Map<String,String> protocols = new HashMap<String,String>();
	
	public boolean addSynchronizedTerminationAction = false;
	public CoherentRoles coherentRoleSets = new CoherentRoles();
	private Iterator<String[]> cohIt = null;

	public String toString() {
		String text = "Roles: " + roles + "\n" + "Values: " + values + "\n" + 
				"coherent roles: " + coherentRoles[0] + " " + coherentRoles[1] + "\n" +
				printMap(protocols) + "\nCoherent sets: " + printCoherence();
		return text;
	}
	
	private String printMap(Map<String,String> map) {
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
	
	
	// protocol operations, building blocks that can be used to assemble a protocol
	
	public static String communication(String fromRole, String toRole, String value) {
		return "C(" + fromRole + "," + toRole + "," + value + ")";
	}
	
	public static String lock(String requesterRole, String toRole) {
		return "lock(" + requesterRole + "," + toRole + ")";
	}
	
	public static String unlock(String requesterRole, String toRole) {
		return "unlock(" + requesterRole + "," + toRole + ")";
	}
	
	public static String choice(String operation1, String operation2) {
		return "(" + operation1 + ")+(" + operation2 + ")";
	}
	
	public static String sequence(String operation1, String operation2) {
		return operation1 + "." + operation2;
	}
	
	public static String sequence(List<String> operations) {
		return String.join(".", operations);
	}
	
	// operations related to local protocols
	
	public static String send(String fromRole, String toRole, String value) {
		return "send(" + fromRole + "," + toRole + "," + value + ")";
	}
	
	public static String receive(String fromRole, String toRole, String value) {
		return "receive(" + fromRole + "," + toRole + "," + value + ")";
	}
	
	public static String tau() {
		return "tau";
	}
	
	// variable set management methods
	
	/**
	 * Add a next sequential operation to a protocol.  
	 * Please use the static methods that comprise building blocks to generate valid operation input.
	 * @param protocol name of the protocol to which the operation is added
	 * @param operation operation content
	 */
	public void addToProtocol(String protocol, String operation) {
		String text = protocols.get(protocol);
		String sequenceOperator = text.equals("") ? "" : ".";
		text = text.concat(sequenceOperator + operation);
		protocols.put(protocol, text);
	}
	
	public void registerProtocol(String protocol) {
		if (!protocols.containsKey(protocol)) {
			protocols.put(protocol, "");
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
	
	// below methods added to support test purposes
	
	
	public void addCommunicationToProtocol(String protocol, String fromRole, String toRole, String value) {
		registerProtocol(protocol);
		registerRole(fromRole);
		registerRole(toRole);
		registerValue(value);
		addToProtocol(protocol,communication(fromRole, toRole, value));
	}
		
	public void addLockToProtocol(String protocol, String requesterRole, String toRole) {
		registerProtocol(protocol);
		registerRole(requesterRole);
		registerRole(toRole);
		addToProtocol(protocol, lock(requesterRole,toRole));
	}
	
	public void addUnlockToProtocol(String protocol, String requesterRole, String toRole) {
		registerProtocol(protocol);
		registerRole(requesterRole);
		registerRole(toRole);
		addToProtocol(protocol, unlock(requesterRole, toRole));	
	}
	
	public void createNonCoherentTestset() {
		addCommunicationToProtocol("P1", "Ragent1", "R1", "one");
		addCommunicationToProtocol("P1", "R1", "Ragent1", "ack");
		addCommunicationToProtocol("P1", "Ragent1", "R2", "one");
		addCommunicationToProtocol("P1", "R2", "Ragent1", "ack");
		
		addCommunicationToProtocol("P2", "Ragent2", "R1", "two");
		addCommunicationToProtocol("P2", "R1", "Ragent2", "ack");
		addCommunicationToProtocol("P2", "Ragent2", "R2", "two");
		addCommunicationToProtocol("P2", "R2", "Ragent2", "ack");
		
		setCoherent("R1","R2");
	}
	
	public void createCoherentTestset() {
		addLockToProtocol("P1","Ragent1", "R1");
		addCommunicationToProtocol("P1", "Ragent1", "R1", "one");
		addCommunicationToProtocol("P1", "R1", "Ragent1", "ack");
		addCommunicationToProtocol("P1", "Ragent1", "R2", "one");
		addCommunicationToProtocol("P1", "R2", "Ragent1", "ack");
		addUnlockToProtocol("P1","Ragent1", "R1");
		
		addLockToProtocol("P2","Ragent2", "R1");
		addCommunicationToProtocol("P2", "Ragent2", "R1", "two");
		addCommunicationToProtocol("P2", "R1", "Ragent2", "ack");
		addCommunicationToProtocol("P2", "Ragent2", "R2", "two");
		addCommunicationToProtocol("P2", "R2", "Ragent2", "ack");
		addUnlockToProtocol("P2","Ragent2", "R1");
		
		setCoherent("R1","R2");
	}
	
	public void createTestset1() {  // global protocol
		testset1Setup();
		registerProtocol("P1");
		addCommunicationToProtocol("P1", "R1", "R2", "one");
		addCommunicationToProtocol("P1", "R2", "R3", "one");
		addCommunicationToProtocol("P1", "R3", "R2", "one");
	}
	
	public void createTestset1Bisim() {	// local protocols, is a bisimulation with createTestset1
		testset1Setup();
		registerProtocol("P1");
		registerProtocol("P2");
		registerProtocol("P3");
		addToProtocol("P1", send("R1", "R2", "one"));
		addToProtocol("P1", tau());
		addToProtocol("P1", tau());
		
		addToProtocol("P2", receive("R1", "R2", "one"));
		addToProtocol("P2", send("R2", "R3", "one"));
		addToProtocol("P2", receive("R3", "R2", "one"));
		
		addToProtocol("P3", tau());
		addToProtocol("P3", receive("R2", "R3", "one"));
		addToProtocol("P3", send("R3", "R2", "one"));
	}
	
	public void createTestset1NotBisim() {
		createTestset1Bisim();
		addToProtocol("P1", send("R1", "R3", "one"));
	}
	
	private void testset1Setup() {

		registerRole("R1");
		registerRole("R2");
		registerRole("R3");
		registerValue("one");
		setCoherent("R1","R2");
		addSynchronizedTerminationAction = false;
	}
	
	
	
	
	
	
}
