package myWhatsServer;


import java.io.*;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserCatalog {

    /**
     * instancias
     */

    private static UserCatalog INSTANCE = null;

    private Map<String, String> mapUsers;
    private Map<String, MyWhatsUser> mapObjs;
    private Key key;
    private MacGenerator mac;

    private UserCatalog(Key key) throws IOException {
        mapUsers = new HashMap<String, String>();
        mapObjs = new HashMap<String, MyWhatsUser>();
        mac = new MacGenerator();
        this.key = key;
        //loadState();
    }

    /**
     * construtor
     *
     * @return instancia do UserCatalog
     * @throws IOException
     */

    public static UserCatalog getInstance(Key key) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new UserCatalog(key);
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

    public boolean register(String user, String pwd) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        File f = new File("log/passwords.txt");

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        SecureRandom rand = new SecureRandom();

        int salt = rand.nextInt((999999 - 100000) + 1) + 100000;
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
                return mac.createMac(f, "mac/passwords.txt");
            } catch (IOException e) {
                throw new IOException("receiveMessage error");
            }
        } else if (!f.exists() && !f.isDirectory()) {
            try (PrintStream output = new PrintStream(f)) {
                output.printf("%s", user + ":" + salt + ":");
                output.printf("%s\r\n", pwd);
                MyWhatsUser utilizador = new MyWhatsUser(user, pwd, salt);
                mapObjs.put(user, utilizador);
                output.close();
                // TODO VERIFICAR SE AQUI TAMBEM LEVA RETURN TRUE OU NAO
                return mac.createMac(f, "mac/passwords.txt");
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

    public boolean login(String user, String pwd) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            System.out.println("login");
            System.out.println("user = " + user);
            if (mapUsers.containsKey(user)) {
            	MyWhatsUser userA = mapObjs.get(user);
            	int salt = userA.getSalt();
            	pwd = pwd + ":" +  salt;
            	MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            	messageDigest.update(pwd.getBytes());
                // pwd passa a ser string da hash para comparacao
                pwd = new String(messageDigest.digest());
                System.out.println("pass guardada = " + mapUsers.get(user));
                System.out.println("pass hashada no login = " + pwd);
                return mapUsers.get(user).equals(pwd);
            } else {
                register(user, pwd);
                return true;
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("ficheiro nao encontrado");
        }
    }

    public boolean contactExists(String contact) {
        return mapUsers.containsKey(contact);
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

