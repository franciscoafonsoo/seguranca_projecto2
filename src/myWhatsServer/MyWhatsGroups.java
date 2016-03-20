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

	private String owner;
	
	private List<String> users = new ArrayList<String>();

	/**
	 * construtor
	 *
	 */

	
	public MyWhatsGroups(String owner) {

		this.owner = owner;
	}

	/**
	 * opcao -a user group
	 * adicionar membro a um grupo
	 *
	 */

	public boolean addGroup(String user) {
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

	public boolean removeGroup(String user) {
		if (users.contains(user)) {
			return users.remove(user);
		}
		else {
			return false;
		}
	}
}
