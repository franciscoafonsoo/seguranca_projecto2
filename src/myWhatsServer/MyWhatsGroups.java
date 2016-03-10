package myWhatsServer;

import java.util.ArrayList;
import java.util.List;

/**
 * cria e gera grupos
 *
 */


public class MyWhatsGroups {

	/**
	 * instancias
	 *
	 */

	private MyWhatsUser owner;
	
	private List<MyWhatsUser> users = new ArrayList<MyWhatsUser>();

	/**
	 * construtor
	 *
	 */

	
	public MyWhatsGroups(MyWhatsUser owner) {

		this.owner = owner;
	}

	/**
	 * opcao -a user group
	 * adicionar membro a um grupo
	 *
	 */

	public boolean addGroup(MyWhatsUser user) {
		if (users.contains(user)) {
			return false;
		}
		else {
			users.add(user);
			return true;
		}
	}

	/**
	 * opcao -d user group
	 * remover membro de um grupo
	 *
	 */

	public void removeGroup(String group, String user) {

	}
}
