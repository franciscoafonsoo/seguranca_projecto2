package myWhatsServer;


import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
        mac = MacGenerator.getInstance();
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
                output.flush();
                output.close();
                return mac.createMac(f, "mac/passwords.txt");
            } catch (IOException e) {
                throw new IOException("receiveMessage error");
            }
        } else if (!f.exists() && !f.isDirectory()) {
            try (PrintStream output = new PrintStream(f)) {
                output.printf("%s", user + ":" + salt + ":");
                output.printf("%s", pwd);
                output.println();
                MyWhatsUser utilizador = new MyWhatsUser(user, pwd, salt);
                mapObjs.put(user, utilizador);
                output.flush();
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

//            File password = new File("log/passwords.txt");
//            // Path filePath = password.toPath();
//
//            FileReader fileReader = new FileReader(password);
//            BufferedReader br = new BufferedReader(fileReader);
//            String linee;
//            String[] stringArray = new String[100];
//            // if no more lines the readLine() returns null
//            int j = 0;
//            while ((linee = br.readLine()) != null) {
//                stringArray[j] = linee;
//                j = j + 1;
//            }
//
//            // List<String> stringList = Files.readAllLines(filePath);
//            // String[] stringArray = stringList.toArray(new String[]{});
//            int i =0;
//            boolean found = false;
//            while (i<stringArray.length && !(found)) {
//            	String line = stringArray[i];
//            	String[] stuff = line.split(":");
//            	System.out.println(user + "!!!!!!!" + stuff[0]);
//            	if (user.equals(stuff[0])) {
//            		String check = pwd + ":" + stuff[1];
//            		System.out.println(check);
//            		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//                	messageDigest.update(pwd.getBytes());
//                    // pwd passa a ser string da hash para comparacao
//                    String newpwd = new String(messageDigest.digest());
//                    System.out.println(newpwd);
//                    System.out.println(stuff[2]);
//                    if (newpwd.equals(stuff[2])) {
//                    	return true;
//                    }
//                    else {
//                    	return false;
//                    }
//            		
//            	}
//            }
//            register(user, pwd);
//            return true;
//

        } catch (FileNotFoundException e) {
        	register(user, pwd);
        	return true;
        }
        
        catch (NoSuchFileException e) {
        	register(user, pwd);
        	return true;
        }
        
        catch (MalformedInputException e) {
        	File password = new File("log/passwords.txt");
        	BufferedReader brTest = new BufferedReader(new FileReader(password));
        	String line = brTest.readLine();
        	String[] stuff = line.split(":");
        	System.out.println(user + "!!!!!!" + stuff[0]);
        	if (user.equals(stuff[0])) {
        		String check = pwd + ":" + stuff[1];
        		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            	messageDigest.update(pwd.getBytes());
                // pwd passa a ser string da hash para comparacao
                String newpwd = new String(messageDigest.digest());
                System.out.println(newpwd + "!!!!!" + stuff[2]);
                if (newpwd.equals(stuff[2])) {
                	return true;
                }
                else {
                	return false;
                }
        	}
        	register(user, pwd);
        	return true;
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

