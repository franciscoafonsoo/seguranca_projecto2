package myWhatsServer;

import java.util.HashMap;
import java.util.Map;

public class GroupCatalog {

	private static GroupCatalog INSTANCE;
	
	private Map<String,MyWhatsGroups> mapGroups;
	
	private GroupCatalog() {
		 mapGroups = new HashMap<String, MyWhatsGroups>();
	}
	
	public static GroupCatalog getInstance() {
		if (INSTANCE==null) {
			return new GroupCatalog();
		}
		else {
			return INSTANCE;
		}
	}
	
	public boolean addUserToGroup (String creator, String user, String group) {
		System.out.println("adicionar ao grupo");
		if (mapGroups.containsKey(group)){
			MyWhatsGroups grupo = mapGroups.get(group);
			return grupo.addGroup(user);
		}
		else {
			MyWhatsGroups grupo = new MyWhatsGroups(creator);
			mapGroups.put(group, grupo);
			return grupo.addGroup(user);
		}
	}
	
	public boolean hasGroup(String group) {
		return mapGroups.containsKey(group);
	}
	
	public boolean removeFromGroup(String group, String user) {
		MyWhatsGroups groups = mapGroups.get(group);
		System.out.println("user:");
		System.out.println(user);
		return groups.removeGroup(user);
	}
	
	
}
