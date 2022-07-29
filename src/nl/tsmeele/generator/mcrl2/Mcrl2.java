package nl.tsmeele.generator.mcrl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tsmeele.compiler.TargetCode;
import nl.tsmeele.compiler.Value;
import nl.tsmeele.compiler.Variable;

/**
 * Responsible for translation of DSL concepts to mCRL2 concepts.
 * 
 * @author Ton Smeele
 *
 */
public class Mcrl2 {
	private int mSeqNo = 0; // sequence number to produce unique new mCRL2 roles/values 
	
	// map from a compiler Role (+ associated IrodsAtttribute) to a rolename acceptable to mcrl2
	private Map<String,String> roleMapper = new HashMap<String,String>();
	
	// registration of all roles (with their current value) and all generated values
	private Map<String,String> mRoleValue = new HashMap<String,String>();
	private ArrayList<String> mValues = new ArrayList<String>();
	
	// sets of mcrl2 roles that need to remain coherent with each other
	private CoherentRoles coherent = new CoherentRoles();

	// protocol definitions
	private Map<String,String> protocols = new HashMap<String,String>();
	
	
	
	
	public String toString() {
		String text = "Variable Set content:\n" +
				"Roles:\n" + printMap(roleMapper) +
				"Values:\n" + mValues + "\n" +
				"Protocols:\n" + printMap(protocols) +
				"Coherent:\n" + printCoherent();
		return text;
	}
	
	private String printCoherent() {
		String text = "";
		for (ArrayList<String> roles : coherent) {
			text = text.concat(roles.toString() + "\n");
		}
		return text;
	}
	
	private String printMap(Map<String,String> map) {
		String text = "";
		for (String key : map.keySet()) {
			text = text.concat("  " + key + " -> " + map.get(key) + "\n");
		}
		return text;
	}
	
	
	
	public void codeProtocol(Value protocolCode) {
		String protocolDefinition = renderCode(protocolCode);
		// register the protocol body as a definition under a new protocol name
		String mProtocol = Mcrl2VariableSet.protocolPrefix + Integer.toString(mSeqNo++);
		protocols.put(mProtocol, protocolDefinition);
		// reset the "values" of all roles so that the next protocol is independent of current protocol
		mRoleValue = new HashMap<String,String>();
	}
	
	public static String renderCode(Value protocolCode) {
		// process code fragments into a protocol by joining them as a sequence of operations
		LinkedList<TargetCode> fragments = protocolCode.getCode();
		List<String> renderedFragments = new ArrayList<String>();
		for (TargetCode f : fragments) {
			String rendered = f.renderAsString();
			if (rendered == null || rendered.length() == 0) {
				// filter empty fragments
				continue;
			}
			renderedFragments.add(f.renderAsString());
		}
		return String.join(".", renderedFragments);
	}
	
	
	public void codeCoherent(ArrayList<Variable> roles) {
		ArrayList<String> roleList = new ArrayList<String>();
		for (Variable role : roles) {
			roleList.add(mRole(role));
		}
		coherent.add(roleList);	
	}
	
	public String codeCommunication(Variable fromRole, Variable toRole) {
		String mFrom = mRole(fromRole);
		String mTo = mRole(toRole);
		if (mFrom.equals(mTo)) {
			// we will not generate any code for internal communications
			return "";
		}
		// process the communication:
		//  1) update the toRole with the value sent by the fromRole
		mRoleValue.put(mTo, mValue(mFrom));
		//  2) return targetCode for this operation
		return "C(" + mFrom + "," + mTo + "," + mValue(mFrom) + ")";
	}
	

	
	
	public String codeLock(Variable requesterRole, Variable targetRole) {
		String mFrom = mRole(requesterRole);
		String mTo = mRole(targetRole);
		return "lock(" + mFrom + "," + mTo + ")";
	}


	
	public String codeUnlock(Variable requesterRole, Variable targetRole) {
		String mFrom = mRole(requesterRole);
		String mTo = mRole(targetRole);
		return "unlock(" + mFrom + "," + mTo + ")";
	}
	
	
	
	public Mcrl2VariableSet populateModel() {
		if (roleMapper.keySet().size() < 2) {
			// we require at least two roles to populate a coherence program model
			return null;
		}
		Mcrl2VariableSet m = new Mcrl2VariableSet();
		for (String role : roleMapper.keySet()) {
			String mRole = roleMapper.get(role);
			m.registerRole(mRole);
		}
		for (String value : mValues) {
			m.registerValue(value);
		}
		for (String protocolName : protocols.keySet()) {
			m.registerProtocol(protocolName, protocols.get(protocolName));
		}
		m.registerCoherentRoleSets(coherent);
		// initialize coherence with arbitrary roles so that model is 'complete'
		m.setCoherent(m.roles.get(0), m.roles.get(1));
		return m;
	}
	

	
	
	
	private String mRole(Variable role) {
		String roleName = canonicalName(role);
		String mcrlRole = roleMapper.get(roleName);
		if (mcrlRole != null) {
			// role already registered
			return mcrlRole;
		}
		// register the role 
		mcrlRole = Mcrl2VariableSet.rolePrefix + Integer.toString(mSeqNo++);
		roleMapper.put(roleName, mcrlRole);
		// register the initial value for the role
		mRoleValue.put(mcrlRole,mValue(mcrlRole));
		return mcrlRole;	
	}
	
	private String mValue(String mRole) {
		String mcrlValue = mRoleValue.get(mRole);
		if (mcrlValue != null) {
			return mcrlValue;
		}
		// role does not yet have a value, we need to create a new one
		mcrlValue = Mcrl2VariableSet.valuePrefix + Integer.toString(mSeqNo++); 
		mValues.add(mcrlValue);
		mRoleValue.put(mRole, mcrlValue);
		return mcrlValue;
	}
	
	
	private String canonicalName(Variable role) {
		String name = role.getName();
		Value irodsAttribute = role.getValue();
		if (irodsAttribute != null) {
			return name + "." + irodsAttribute.getVariable().getName();
		}
		return name;
	}


}
