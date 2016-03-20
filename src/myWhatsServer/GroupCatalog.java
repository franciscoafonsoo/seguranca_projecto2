package myWhatsServer;

import java.util.HashMap;
import java.util.Map;

public class GroupCatalog {

	private static GroupCatalog INSTANCE;
	
	private Map<String,MyWhatsGroups> mapGroups = new HashMap<String, MyWhatsGroups>();
	
	private GroupCatalog() {
		INSTANCE = new GroupCatalog();
	}
	
	public GroupCatalog getInstance() {
		if (INSTANCE==null) {
			return new GroupCatalog();
		}
		else {
			return INSTANCE;
		}
	}
	
	public boolean addUserToGroup (String creator, String user, String group) {
		if (mapGroups.containsKey(group)){
			MyWhatsGroups grupo = mapGroups.get(group);
			return grupo.addGroup(user);
		}
		else {
			MyWhatsGroups grupo = new MyWhatsGroups(creator);
			return grupo.addGroup(user);
		}
	}
	
	public boolean hasGroup(String group) {
		return mapGroups.containsKey(group);
	}
	
	public boolean removeFromGroup(String group, String user) {
		MyWhatsGroups groups = mapGroups.get(group);
		return groups.removeGroup(user);
	}
	
	
}
