package myWhatsServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCatalog {

	/**
	 * instancias
	 */

	private static UserCatalog INSTANCE = null;

	private Map<String,String> mapUsers;
	private UserCatalog() throws IOException{ 
		mapUsers = new HashMap<>(); 
		loadState();
	}

	/**
	 * construtor
	 * @return instancia do UserCatalog
	 * @throws IOException 
     */
	
	public static UserCatalog getInstance() throws IOException {
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
	 * @throws IOException 
     */
	
	public boolean register(String user, String pwd) throws IOException {

        File f = new File("log/passwords.txt");

        if (f.exists() && !f.isDirectory()) {
            try (PrintWriter output = new PrintWriter(new FileWriter(f, true))) {
                output.printf("%s", user + ":");
                output.printf("%s\r\n", pwd);

                return true;
            } catch (IOException e) {
                throw new IOException("receiveMessage error");
            }
        }
        return false;
    }

	/**
	 * fazer login de um utilizador
	 * @param user nome utilizador
	 * @param pwd pass utilizador
     * @return works or not
	 * @throws IOException 
     */
	
	public boolean login(String user, String pwd) throws IOException {
		try {
			
			if (mapUsers.containsKey(user)) {
				if (mapUsers.get(user).equals(pwd)){
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
		catch (FileNotFoundException e){
			throw new FileNotFoundException("ficheiro nao encontrado");
		}
	}
	
	public boolean contactExists(String contact) {
		if (mapUsers.containsKey(contact)) {
			return true;
		}
		return false;
	}
	
	private void loadState() throws IOException {
		Path path = Paths.get("log/passwords.txt");
        List<String> lines = Files.readAllLines(path);
        for (String e : lines) {
        	System.out.println(e);
        	String[] user = e.split(":");
        	mapUsers.put(user[0], user[1]);
        }
	}
	}

