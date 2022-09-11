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
	private Mcrl2VariableSet vars = new Mcrl2VariableSet();
	
	// register of roles, map from a compiler role name to its mcrl2 equivalent name 
	private Map<String,String> roleMapper = new HashMap<String,String>();
	
	private Map<String,ArrayList<String>> roleAttributes = new HashMap<String,ArrayList<String>>();
	private Map<String,ArrayList<String>> roleAttrValues = new HashMap<String,ArrayList<String>>();
	
	
	
	public String toString() {
		String text = "Mcrl2 content:\n" +
				"Mapped roles:\n" + printMap(roleMapper) +
				"Content of VariableSet:\n" + vars.toString();
		return text;
	}
	
	
	private String printMap(Map<String,String> map) {
		String text = "";
		for (String key : map.keySet()) {
			String mRole = map.get(key);
			text = text.concat("  " + key + " -> " + mRole + "\n");
			text = text.concat("    attrs: " + roleAttributes.get(mRole).toString() + "\n");
		}
		return text;
	}
	
	
	
	public void codeProtocol(Value protocolCode) {
		String protocolDefinition = renderCode(protocolCode);
		// register the protocol body as a definition under a new protocol name
		String mProtocol = Mcrl2VariableSet.protocolPrefix + Integer.toString(mSeqNo++);
		vars.protocols.put(mProtocol, protocolDefinition);
		// reset the "values" of all roles so that the next protocol is independent of current protocol
		for (String role : vars.roles) {
			ArrayList<String> attrs = roleAttrValues.get(role);
			for (int i = 0; i < attrs.size(); i++) {
				attrs.set(i, createMcrlValue());
			}
			roleAttrValues.put(role, attrs);
		}
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
		AttributeList attrs = new AttributeList();
		for (Variable role : roles) {
			attrs.add(mRole(role));
		}
		vars.coherentAttrSets.add(attrs);	
	}
	
	public String codeCommunication(Variable fromRole, Variable toRole) {
		// lookup mCRL role/attribute
		Attribute from = mRole(fromRole);
		Attribute to = mRole(toRole);
		
		// process the communication:
		//  1) update the toRole with the value sent by the fromRole
		String mValue = getMcrlValue(from);
		setMcrlValue(to, mValue);
		//  2) return targetCode for this operation
		if (from.equals(to)) {
			// we will not generate any mCRL2 code for internal communications
			return "";
		}
		return "C(" + from.getRole() + "," + to.getRole() + "," + to.getAttr() + "," + mValue + ")";
	}
	

	
	
	public String codeLock(Variable requesterRole, Variable targetRole) {
		String mFrom = mRole(requesterRole).getRole();
		Attribute target = mRole(targetRole);
		String mTo = target.getRole();
		String mAttr = target.getAttr();
		return "lock(" + mFrom + "," + mTo + "," + mAttr + ")";
	}


	
	public String codeUnlock(Variable requesterRole, Variable targetRole) {
		String mFrom = mRole(requesterRole).getRole();
		Attribute target = mRole(targetRole);
		String mTo = target.getRole();
		String mAttr = target.getAttr();
		return "unlock(" + mFrom + "," + mTo + "," + mAttr + ")";
	}
	
	
	
	public Mcrl2VariableSet populateModel() {
		if (vars.roles.size() < 2) {
			// we require at least two roles to populate a coherence program model
			return null;
		}
		// initialize coherence with arbitrary role/attr so that model is 'complete'
		Attribute attr1 = new Attribute(vars.roles.get(0), 0);
		Attribute attr2 = new Attribute(vars.roles.get(1), 0);
		vars.setCoherent(attr1, attr2);
		return vars;
	}

	
	//  ---------------- translate to mcrl2 name and register
	
	


	
	
	private Attribute mRole(Variable role) {
		String roleName = role.getName();
		String attrName = "";  // default attribute name
		Value irodsAttribute = role.getValue();
		if (irodsAttribute != null) {
			attrName = irodsAttribute.getVariable().getName();
		}
		String mcrlRole = roleMapper.get(roleName);
		if (mcrlRole == null) {
			// register as new role
			mcrlRole = createMcrlRole(roleName);
		}
		ArrayList<String> attrs = roleAttributes.get(mcrlRole);
		int attrNo = 0;
		if (attrs.contains(attrName)) {
			// attribute already registered, just return what we have found
			attrNo = attrs.indexOf(attrName);
			return new Attribute(mcrlRole, attrNo);
		}
		// attribute is new, we need to map it to a new mCRL2 attribute for this role
		attrNo = attrs.size(); // attributes are referenced by their index position
		attrs.add(attrNo, attrName);
		roleAttributes.put(mcrlRole, attrs);
		// create and register an mCRL value for the new attribute
		ArrayList<String> attrValues = roleAttrValues.get(mcrlRole);
		attrValues.add(attrNo,createMcrlValue());
		roleAttrValues.put(mcrlRole,attrValues);
		return new Attribute(mcrlRole,attrNo);	
	}
	
	
	private String createMcrlRole(String roleName) {
		String mRole = Mcrl2VariableSet.rolePrefix + Integer.toString(mSeqNo++);
		// register as mCRL2 role
		vars.roles.add(mRole);	
		// facilitate mCRL2 role name lookup
		roleMapper.put(roleName, mRole); 
		// keep an index of role related attributes and their values
		ArrayList<String> attrs = new ArrayList<String>();
		roleAttributes.put(mRole, attrs);
		ArrayList<String> attrValues = new ArrayList<String>();
		roleAttrValues.put(mRole, attrValues); 
		return mRole;
	}
	
	private String createMcrlValue() {
		String mValue = Mcrl2VariableSet.valuePrefix + Integer.toString(mSeqNo++);
		vars.values.add(mValue);
		return mValue;
	}
	
	private String getMcrlValue(Attribute mRole) {
		ArrayList<String> attrs = roleAttrValues.get(mRole.getRole());
		return attrs.get(mRole.getAttrNo());
	}
	
	private void setMcrlValue(Attribute mRole, String mValue) {
		ArrayList<String> attrs = roleAttrValues.get(mRole.getRole());
		attrs.set(mRole.getAttrNo(), mValue);
		roleAttrValues.put(mRole.getRole(), attrs);
	}


}
