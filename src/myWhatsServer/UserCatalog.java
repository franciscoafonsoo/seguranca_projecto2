package myWhatsServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserCatalog {

    /**
     * instancias
     */

    private static UserCatalog INSTANCE = null;

    private Map<String, String> mapUsers;
    private Map<String, MyWhatsUser> mapObjs;

    private UserCatalog() throws IOException {
        mapUsers = new HashMap<String, String>();
        mapObjs = new HashMap<String, MyWhatsUser>();
        //loadState();
    }

    /**
     * construtor
     *
     * @return instancia do UserCatalog
     * @throws IOException
     */

    public static UserCatalog getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new UserCatalog();
        }
        return INSTANCE;
    }

    /**
     * registar um novo user no catalogo
     *
     * @param user nome utilizador
     * @param pwd  pass utilizador
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */

    public boolean register(String user, String pwd) throws IOException, NoSuchAlgorithmException {

        File f = new File("log/passwords.txt");

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    	Random rand = new Random();
        int salt = rand.nextInt((999999 - 100000) +1) +100000;
        System.out.println("salt=" + salt);
     	// guarda internamente os bytes (ja eh sha-256)
        pwd = pwd + ":" + salt;
        System.out.println("pwd=" + pwd);
        messageDigest.update(pwd.getBytes());
        // pwd passa a ser string da hash para comparacao
        pwd = new String(messageDigest.digest());
        System.out.println(pwd);
        if (f.exists() && !f.isDirectory()) {
            try (PrintWriter output = new PrintWriter(new FileWriter(f, true))) {
            	
                output.printf("%s", user + ":" + salt + ":");
                output.printf("%s\r\n", pwd);
                mapUsers.put(user, pwd);
                MyWhatsUser utilizador = new MyWhatsUser(user, pwd, salt);
                mapObjs.put(user, utilizador);
                return true;
            } catch (IOException e) {
                throw new IOException("receiveMessage error");
            }
        } else {
            try (PrintStream output = new PrintStream(f)) {
                output.printf("%s", user + ":" + salt + ":");
                output.printf("%s\r\n", pwd);
                MyWhatsUser utilizador = new MyWhatsUser(user, pwd, salt);
                mapObjs.put(user, utilizador);
            } catch (IOException e) {
                throw new IOException("receiveMessage error");
            }
        }
        return false;
    }

    /**
     * fazer login de um utilizador
     *
     * @param user nome utilizador
     * @param pwd  pass utilizador
     * @return works or not
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */

    public boolean login(String user, String pwd) throws IOException, NoSuchAlgorithmException {
        try {
            System.out.println("login");
            System.out.println("user = " + user);
            if (mapUsers.containsKey(user)) {
                if (mapUsers.get(user).equals(pwd)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                register(user, pwd);
                return true;
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("ficheiro nao encontrado");
        }
    }

    public boolean contactExists(String contact) {
        if (mapUsers.containsKey(contact)) {
            return true;
        }
        return false;
    }

    public void associateFile(String user, String file) {
        MyWhatsUser utilizador = mapObjs.get(user);
        utilizador.associateFile(file);
    }

    public void addToGroup(String user, String group) {
        MyWhatsUser utilizador = mapObjs.get(user);
        utilizador.enterGroup(group);
    }

    public List<String> getAllFiles(String user) {
        MyWhatsUser utilizador = mapObjs.get(user);
        return utilizador.getAllFiles();
    }

/**    private void loadState() throws IOException {

 File f = new File("log/passwords.txt");

 if(f.exists() && !f.isDirectory()) {

 Path path = Paths.get("log/passwords.txt");
 List<String> lines = Files.readAllLines(path);

 for (String e : lines) {
 System.out.println(e);
 String[] user = e.split(":");
 MyWhatsUser utilizador = new MyWhatsUser(user[0], user[1]);
 mapUsers.put(user[0], user[1]);
 mapObjs.put(user[0], utilizador);
 }
 }
 }
 */
}

