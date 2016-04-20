package myWhatsServer;

import java.io.*;
import java.util.*;


public class MyWhatsUser {

    /**
     * instancias
     */

    private String user;
    private String pwd;
    private int salt;
    private List<String> groups = new ArrayList<String>();
    private List<String> files = new ArrayList<String>();

    /**
     * construtor
     *
     * @param user utilizador
     * @param pwd  password
     */

    public MyWhatsUser(String user, String pwd, int salt) {
        this.user = user;
        this.pwd = pwd;
        this.salt = salt;
    }

    /**
     * escrever um novo utilizador
     *
     * @param user nome utilizador
     * @param pwd  password utilizador
     * @return true or false (not sure yet)
     * @throws FileNotFoundException
     */

    public boolean writeUser(String user, String pwd) throws FileNotFoundException {

        try {
            PrintWriter escrever = new PrintWriter("log/users.txt");
            String userpwd = user + ":" +salt+ ":"+ pwd;
            escrever.println(userpwd);
            escrever.close();
            return true;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("ficheiro nao encontrado");
        }

    }

    /**
     * ler um utilizador. se nao encontrado, chamar writeUser
     *
     * @param user nome utilizador
     * @param pwd  password utilizador
     * @return true or false (not sure yet)
     * @throws FileNotFoundException
     */

    public boolean readUser(String user, String pwd) throws FileNotFoundException {
        try {

            /**
             * em alternativa a este metodo pode-se usar um arraylist, e fazer a comparacao
             */

            Scanner scanner = new Scanner("log/users.txt");
            String userpwd = user + ":" + pwd;

            while (scanner.hasNextLine()) {
                if (userpwd.equals(scanner.nextLine().trim())) {
                    return true;
                } else if (writeUser(user, pwd))
                    return true;
            }
            scanner.close();
            return false;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("ficheiro nao encontrado");
        }
    }

    public boolean enterGroup(String group) {
        if (groups.contains(group)) {
            return false;
        } else {
            groups.add(group);
            files.add(group + ".txt");
            return true;
        }
    }

    public void associateFile(String file) {
        files.add(file);
    }

    public List<String> getAllFiles() {
        return files;
    }
    
    public int getSalt() {
    	return salt;
    }

}
