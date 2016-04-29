package myWhatsServer;


import javax.crypto.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
    private KeyStore keyStore;

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
    
    public void setKeyStore(KeyStore keyStore) {
    	this.keyStore = keyStore;
    }
    
    /**
     * registar um client "user"
     *
     * @throws IOException
     * @throws KeyStoreException 
     * @throws CertificateException 
     */

    public boolean handle(String pedido, String user, ObjectInputStream in, ObjectOutputStream out) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, KeyStoreException, CertificateException, ClassNotFoundException {

        String[] request = pedido.split(":");
        String op = request[0];
        switch (op) {
            case "-m":
                System.out.println("entrar no recvmessage");
                return receiveMessage(request[2], user, request[1], key);
            case "-f":
                //receiveMessage(request[2], user, request[1], key);
                receiveFile(request[2],request[1], user, in,out , key);
                break;
            case "-r":
            	System.out.println("length = " + request.length);
                if (request.length == 1) {
                    shareMessage(user, out, key);
                } else if (request.length == 2) {
                    shareContact(request[1], user, out);
                } else {
                    shareFile(request[1], request[2], user, out,in, key);
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
     * @throws IOException 
     * @throws KeyStoreException 
     */

    public void sendCertificate(ObjectOutputStream out) throws KeyStoreException, IOException {
    	out.writeObject(keyStore.getCertificate("SIServer"));
    }
    
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
     * @throws KeyStoreException 
     * @throws CertificateException 
     */

    private void receiveFile(String fileName, String recvuser, String contact, ObjectInputStream is, ObjectOutputStream out, Key key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException, CertificateException, ClassNotFoundException {

        //try {
        	List<String> alph = new ArrayList<>();
            alph.add(contact);
            alph.add(recvuser);
            java.util.Collections.sort(alph);
            File f = new File("files/" + alph.get(0) + "_" + alph.get(1) + "_" + fileName + ".txt" );
            System.out.println("vou ler pela primeira vez");
        // FileOutputStream fos = new FileOutputStream(f);

        byte[] signature = (byte[]) is.readObject();
        Files.write(Paths.get("signatures/" + fileName + "." + contact), signature);
            
            
        byte[] content = (byte[]) is.readObject();
        Files.write(f.toPath(), content);

/*            int count;
            byte[] bytes = new byte[1024];
            while((count = is.read(bytes))!= -1) {
            	System.out.println(count);
            	fos.write(bytes);
            	
            }*/
            
            System.out.println("ficheiro criado");
            
            FileInputStream certIS = new FileInputStream(new File("certs/" + recvuser + ".cert"));
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate c = (X509Certificate) fact.generateCertificate(certIS);
            out.writeObject(c);
            System.out.println("vou ler!");
            byte[] keyReceiver = new byte[256];
            is.read(keyReceiver);
            System.out.println("li a primeira");
            byte[] keySender = new byte[256];
            is.read(keySender);
            System.out.println("li a segunda");
            File ficheiroSender = new File("chaves/" +  fileName + ".key." + contact);
            FileOutputStream ficheiroOS = new FileOutputStream(ficheiroSender);
            ficheiroOS.write(keySender);
            System.out.println("escrevi a primeira");
            File ficheiroReceiver = new File("chaves/" + fileName + ".key." + recvuser);
            FileOutputStream recvOS = new FileOutputStream(ficheiroReceiver);
            recvOS.write(keyReceiver);
            
            
            
            
            System.out.println("nome = " + fileName);
            System.out.println("contact = " + contact);
            
            
            //Files.write(f.toPath(), content);
//        } catch (IOException e) {
//            throw new IOException("receiveFile error");
//        }
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
     * @throws ClassNotFoundException 
     */

    private void shareFile(String contact, String fileName, String user, ObjectOutputStream out,ObjectInputStream in, Key key) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {

        //try {

        	List<String> alph = new ArrayList<>();
            alph.add(contact);
            alph.add(user);
            java.util.Collections.sort(alph);
            File f = new File("files/" + alph.get(0) + "_" + alph.get(1) + "_" + fileName + ".txt");

            Path path = Paths.get(f.getCanonicalPath());
            byte[] data = Files.readAllBytes(path);

/*            FileInputStream fileIS = new FileInputStream(f);
            int count;
            byte[] buffer = new byte[1024];
            while((count= fileIS.read(buffer))!=-1) {
            	out.write(buffer);
            }*/
            
            // byte[] keybuffer = new byte[256];
            
            byte[] signature = Files.readAllBytes(Paths.get("signatures/" + fileName + "." + "contact"));
            out.writeObject(signature);
            
            
            File keyFile = new File("chaves/" + fileName + ".key." + user);

            Path pathkey = Paths.get(keyFile.getCanonicalPath());
            byte[] datakey = Files.readAllBytes(pathkey);

            // FileInputStream keyIS = new FileInputStream(keyFile);
            // keyIS.read(keybuffer);
            out.writeObject(datakey);
            out.writeObject(data);
            
//        } catch (IOException e) {
//            throw new IOException("receiveFile error");
//        }
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
