package myWhatsServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GroupCatalog {

	private static GroupCatalog INSTANCE;
	
	private UserCatalog userCat;
	private Map<String,MyWhatsGroups> mapGroups;
	
	private GroupCatalog() throws IOException {
		 mapGroups = new HashMap<String, MyWhatsGroups>();
		 userCat = UserCatalog.getInstance();
	}
	
	public static GroupCatalog getInstance() throws IOException {
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
			userCat.addToGroup(user, group);
			return grupo.addGroup(user);
		}
		else {
			MyWhatsGroups grupo = new MyWhatsGroups(creator);
			mapGroups.put(group, grupo);
			userCat.addToGroup(creator, group);
			userCat.addToGroup(user, group);
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
