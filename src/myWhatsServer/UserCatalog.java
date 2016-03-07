package myWhatsServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCatalog {

	private static UserCatalog INSTANCE = null;
	
	
	private Map<String,String> mapUsers;
	private UserCatalog() {
		mapUsers = new HashMap<String, String>();
	}
	
	public static UserCatalog getInstance() {
		if(INSTANCE.equals(null)) {
			INSTANCE = new UserCatalog();
		}
		return INSTANCE;
	}
	
	public void register(String user, String pwd) {
		MyWhatsUser newuser = new MyWhatsUser(user, pwd);
		mapUsers.put(user, pwd);
	}
	
	public boolean login(String user, String pwd) {
		if(mapUsers.containsKey(user)){
			if(mapUsers.get(user).equals(pwd)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			register(user, pwd);
			return true;
		}
	}
}
