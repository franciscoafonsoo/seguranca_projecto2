package myWhatsServer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
	 * @throws FileNotFoundException 
     */
	
	public void register(String user, String pwd) throws FileNotFoundException {
		MyWhatsUser newuser = new MyWhatsUser(user, pwd);
		try {
			PrintWriter escrever = new PrintWriter("log/users.txt");
			String userpwd = user + ":" + pwd;
			escrever.println(userpwd);
		}
		catch (FileNotFoundException e) {
			throw new FileNotFoundException("ficheiro nao encontrado");
		}
		mapUsers.put(user, pwd);
	}

	/**
	 * fazer login de um utilizador
	 * @param user nome utilizador
	 * @param pwd pass utilizador
     * @return works or not
	 * @throws FileNotFoundException 
     */
	
	public boolean login(String user, String pwd) throws FileNotFoundException {
		if(mapUsers.containsKey(user)){
			if(mapUsers.get(user).equals(pwd)) {
				Scanner scanner = new Scanner("log/users.txt");
				String userpwd = user + ":" + pwd;

				while(scanner.hasNextLine()){
					if(userpwd.equals(scanner.nextLine().trim())){
						return true;
					}
					else {
						
					}
						
				}
				return false;
				
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
