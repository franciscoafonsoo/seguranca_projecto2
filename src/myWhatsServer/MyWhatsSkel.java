package myWhatsServer;


import javax.crypto.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyWhatsSkel {

    /**
     * instancias
     */

    private UserCatalog userCat;
    private GroupCatalog groupCat;
    private Key key;
    private EncryptFile encrypter;

    /**
     * construtor
     *
     * @throws IOException
     */

    public MyWhatsSkel(String pass) throws IOException {
        this.key = createkey(pass);
        userCat = UserCatalog.getInstance(key);
        groupCat = GroupCatalog.getInstance(key);
        encrypter = new EncryptFile(key);
    }

    /**
     * autenticar um cliente "user"
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */

    public String login(String user, String pwd) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        if (userCat.login(user, pwd)) {
            return "OK";
        } else {
            return "NOK";
        }
    }

    /**
     * registar um client "user"
     *
     * @throws IOException
     */

    public boolean handle(String pedido, String user, ObjectInputStream in, ObjectOutputStream out) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        String[] request = pedido.split(":");
        String op = request[0];
        switch (op) {
            case "-m":
                System.out.println("entrar no recvmessage");
                return receiveMessage(request[2], user, request[1], key);
            case "-f":
                receiveMessage(request[2], user, request[1], key);
                receiveFile(request[2],request[1], user, in, key);
                break;
            case "-r":
                if (request.length == 1) {
                    shareMessage(user, out, key);
                } else if (request.length == 2) {
                    shareContact(request[1], user, out);
                } else {
                    shareFile(request[1], request[2], user, out, key);
                }
                break;
            case "-a":
                addToGroup(user, request[1], request[2], key);
                break;
            case "-d":
                removeUserFromGroup(request[1], request[2], key);
                break;
        }
        return true;

    }

    /**
     * opcao -m
     * recebe mensagem e nome do ficheiro partilhado e a autorizacao de acesso ao client "user"
     * escreve para um ficheiro recvuser.txt na pasta msg
     */

    private boolean receiveMessage(String msg, String senduser, String recvuser, Key key) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        System.out.println(userCat.contactExists(recvuser));
        if (userCat.contactExists(recvuser)) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar cal = Calendar.getInstance();
            String dt = dateFormat.format(cal.getTime());

            List<String> alph = new ArrayList<>();
            alph.add(senduser);
            alph.add(recvuser);
            java.util.Collections.sort(alph);
            System.out.println(alph.get(0));
            System.out.println(alph.get(1));
            File f = new File("msg/" + alph.get(0) + "_" + alph.get(1) + ".txt");
            System.out.println("file " + alph.get(0) + "_" + alph.get(1) + ".txt criado");
            //inicio de tentativa
            if (f.exists() && !f.isDirectory()) {
            	String escrever = senduser + "/" + msg + "/" + dt+"/";
                // FileOutputStream output = new FileOutputStream(f);
                encrypter.encryptFile(escrever.getBytes(), f);
                // temp code
                // output.write(escrever.getBytes());
                
            } else {
                userCat.associateFile(alph.get(0), "msg/" + alph.get(0) + "_" + alph.get(1) + ".txt");
                userCat.associateFile(alph.get(1), "msg/" + alph.get(0) + "_" + alph.get(1) + ".txt");
                String escrever = senduser + "/" + msg + "/" + dt+"/";
//               	FileOutputStream output = new FileOutputStream(f);
//               	output.write(escrever.getBytes());
                encrypter.encryptFile(escrever.getBytes(), f);
            }
            //fim de tentativa
            return true;
        } else if (groupCat.hasGroup(recvuser)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar cal = Calendar.getInstance();

            String dt = dateFormat.format(cal.getTime());


            File f = new File("msg/" + recvuser + ".txt");
            System.out.println("file " + recvuser + ".txt criado");
                String escrever = senduser + "/" + msg + "/" + dt+"/";
//               	FileOutputStream output = new FileOutputStream(f);
//               	output.write(escrever.getBytes());
               	encrypter.encryptFile(escrever.getBytes(), f);
            return true;
        } else {
            return false;
        }
    }

    /**
     * opcao -f
     * recebe um ficheiro no servidor e da autorizacao de acesso ao client "user"
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     */

    private void receiveFile(String fileName, String recvuser,String contact, ObjectInputStream is, Key key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        try {
        	List<String> alph = new ArrayList<>();
            alph.add(contact);
            alph.add(recvuser);
            java.util.Collections.sort(alph);
            File f = new File("files/" + alph.get(0) + "_" + alph.get(1) + "_" + fileName + ".txt");
            byte[] content = (byte[]) is.readObject();
            encrypter.encryptFile(content, f);
            System.out.println("nome = " + fileName);
            System.out.println("contact = " + contact);
            
            
            //Files.write(f.toPath(), content);
        } catch (IOException e) {
            throw new IOException("receiveFile error");
        } catch (ClassNotFoundException i) {
            throw new ClassCastException("no ideia");
        }
    }

    /**
     * opcao -r
     * partilha o nome ficheiro/mensagem trocada por outro client "user"
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     */

    private void shareMessage(String user, ObjectOutputStream out, Key key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        List<String> files = userCat.getAllFiles(user);

        out.writeObject(files.size());
        for (String elem : files) {
            File f = new File(elem);
            if (f.exists() && !f.isDirectory()) {
            	FileInputStream fis = new FileInputStream(f);
            	Cipher c = Cipher.getInstance("AES");
            	//cifra para decifrar o ficheiro
            	c.init(Cipher.DECRYPT_MODE, key);
            	CipherInputStream cis = new CipherInputStream(fis, c);
            	byte[] b = new byte[1024];
            	int i;
            	String s = "";
            	while((i=cis.read(b)) != -1) {
            		s.concat(new String(b));
            	}
            	System.out.println(s);
//                Path path = Paths.get(elem);
//                List<String> lines = Files.readAllLines(path);
//                out.writeObject(lines.get(lines.size() - 1));
            } else {
                out.writeObject("nothing");
            }
        }

    }

    /**
     * opcao -r contact
     * partilhar todas as informacoes entre user e contact
     */

    private void shareContact(String contact, String user, ObjectOutputStream out) throws IOException {

        List<String> alph = new ArrayList<>();
        alph.add(user);
        alph.add(contact);
        java.util.Collections.sort(alph);
        String pesquisa = alph.get(0) + "_" + alph.get(1) + ".txt";

        File f = new File("msg/" + pesquisa);

        try {
            Path path = Paths.get("msg/" + pesquisa);
            List<String> data = Files.readAllLines(path);

            out.writeObject(data.size());
            for (String elem : data) {
                out.writeObject(elem);
            }

        } catch (IOException e) {
            throw new IOException("receiveFile error");
        }
    }

    /**
     * opcao -r contact file
     * enviar o ficheiro com nome fileName, enviado por contact
     * <p>
     * user/contact/file
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */

    private void shareFile(String contact, String fileName, String user, ObjectOutputStream out, Key key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        try {

        	List<String> alph = new ArrayList<>();
            alph.add(contact);
            alph.add(user);
            java.util.Collections.sort(alph);
            File f = new File("files/" + alph.get(0) + "_" + alph.get(1) + "_" + fileName + ".txt");
            File tempFile = encrypter.decryptFile(f);
            
            
            out.writeObject(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())));
        } catch (IOException e) {
            throw new IOException("receiveFile error");
        }
    }

    /**
     * check and make dirs if not yet created
     *
     * @param name nome do dir
     */

    public void dir(String name) throws DirException {
        File log = new File(name);

        if (!(log.exists() && log.isDirectory())) {
            boolean feito = log.mkdirs();
            if (feito) {
                System.out.println(name + " CREATED");
            } else {
                throw new DirException("verificar permissoes, etc.");
            }
        } else
            System.out.println(name + " OK");
    }

    /**
     * check and remove dirs if exists
     *
     * @param name nome do dir
     */

    public void rmdir(String name) {
        File file = new File(name);
        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (String myFile1 : myFiles) {
                File myFile = new File(file, myFile1);
                myFile.delete();
            }
            System.out.println(name + " REMOVED");
        } else {
            System.out.println(name + " NOT FOUND");
        }
    }

    public Key createkey(String pass) {
        Key key = null;
        try {
            File aeskey = new File("aeskey.key");

            if (aeskey.exists() && !aeskey.isDirectory()) {
                // load do keystore
                FileInputStream readkeystore = new FileInputStream("SIServer.keystore");
                KeyStore readcert = KeyStore.getInstance("JKS");
                readcert.load(readkeystore, pass.toCharArray());

                // gera uma chave privada
                Key privatekey = readcert.getKey("SIServer", pass.toCharArray());

                System.out.println(privatekey);
                // cria cifra apartir do certificado
                Cipher cph = Cipher.getInstance("RSA");
                cph.init(Cipher.UNWRAP_MODE, privatekey);

                FileInputStream cenas = new FileInputStream(aeskey);
                ObjectInputStream maiscenas = new ObjectInputStream(cenas);
                //TODO ler o tamanho do ficheiro
                byte[] keyencoded = new byte[256];

                //noinspection ResultOfMethodCallIgnored
                maiscenas.read(keyencoded);

                // cria uma chave
                key = cph.unwrap(keyencoded, "AES", Cipher.SECRET_KEY);

            } else {
                // gera uma chave AES
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128);

                // gera uma chave secreta
                key = kg.generateKey();

                // load do keystore
                FileInputStream readkeystore = new FileInputStream("SIClient.keystore");
                KeyStore readcert = KeyStore.getInstance("JKS");
                readcert.load(readkeystore, pass.toCharArray());

                // obtem certificado
                Certificate cert = readcert.getCertificate("SIClient");

                // cria cifra apartir do certificado
                Cipher cph = Cipher.getInstance("RSA");
                cph.init(Cipher.WRAP_MODE, cert);

                // escrever a chave ja encriptada po ficheiro
                FileOutputStream writeaes = new FileOutputStream(aeskey);
                ObjectOutputStream cenas = new ObjectOutputStream(writeaes);
                byte[] keyencoded = cph.wrap(key);
                cenas.write(keyencoded);
                cenas.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return key;
    }

    

    /**
     * add a user to a group
     */

    private void addToGroup(String creator, String user, String group, Key key) throws IOException {
        groupCat.addUserToGroup(creator, user, group);

    }

    private void removeUserFromGroup(String user, String group, Key key) {
        groupCat.removeFromGroup(group, user);
    }
}
