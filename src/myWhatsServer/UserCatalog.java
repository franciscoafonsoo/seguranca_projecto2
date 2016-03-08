package myWhatsServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCatalog {

	/**
	 * instancias
	 */

	private static UserCatalog INSTANCE = null;

	private Map<String,String> mapUsers;
	private UserCatalog(){ mapUsers = new HashMap<>(); }

	/**
	 * construtor
	 * @return instancia do UserCatalog
     */
	
	public static UserCatalog getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new UserCatalog();
		}
		return INSTANCE;
	}

	/**
	 * registar um novo user no catalogo
	 *
	 * @param user nome utilizador
	 * @param pwd pass utilizador
     */
	
	public void register(String user, String pwd) {
		MyWhatsUser newuser = new MyWhatsUser(user, pwd);
		mapUsers.put(user, pwd);
	}

	/**
	 * fazer login de um utilizador
	 * @param user nome utilizador
	 * @param pwd pass utilizador
     * @return works or not
     */
	
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
