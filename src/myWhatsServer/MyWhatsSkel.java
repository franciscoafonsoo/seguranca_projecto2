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

    /**
     * construtor
     *
     * @throws IOException
     */

    public MyWhatsSkel() throws IOException {
        userCat = UserCatalog.getInstance();
        groupCat = GroupCatalog.getInstance();
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

    public boolean handle(String pedido, String user, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        String[] request = pedido.split(":");
        String op = request[0];
        switch (op) {
            case "-m":
                System.out.println("entrar no recvmessage");
                return receiveMessage(request[2], user, request[1]);
            case "-f":
                receiveMessage(request[2], user, request[1]);
                receiveFile(request[2], in);
                break;
            case "-r":
                if (request.length == 1) {
                    shareMessage(user, out);
                } else if (request.length == 2) {
                    shareContact(request[1], user, out);
                } else {
                    shareFile(request[1], request[2], user, out);
                }
                break;
            case "-a":
                addToGroup(user, request[1], request[2]);
                break;
            case "-d":
                removeUserFromGroup(request[1], request[2]);
                break;
        }
        return true;

    }


    /**
     * opcao -m
     * recebe mensagem e nome do ficheiro partilhado e a autorizacao de acesso ao client "user"
     * escreve para um ficheiro recvuser.txt na pasta msg
     */


    public boolean receiveMessage(String msg, String senduser, String recvuser) throws IOException {

        System.out.println(userCat.contactExists(recvuser));
        if (userCat.contactExists(recvuser)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar cal = Calendar.getInstance();

            String dt = dateFormat.format(cal.getTime());

            List<String> alph = new ArrayList<String>();
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
            	FileOutputStream output = new FileOutputStream(f);
            	output.write(escrever.getBytes());
                
            } else {
                userCat.associateFile(alph.get(0), "msg/" + alph.get(0) + "_" + alph.get(1) + ".txt");
                userCat.associateFile(alph.get(1), "msg/" + alph.get(0) + "_" + alph.get(1) + ".txt");
                String escrever = senduser + "/" + msg + "/" + dt+"/";
               	FileOutputStream output = new FileOutputStream(f);
               	output.write(escrever.getBytes());
                
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
               	FileOutputStream output = new FileOutputStream(f);
               	output.write(escrever.getBytes());
            
            return true;
        } else {
            return false;
        }
    }

    /**
     * opcao -f
     * recebe um ficheiro no servidor e da autorizacao de acesso ao client "user"
     */

    public void receiveFile(String name, ObjectInputStream is) throws IOException {

        try {
            File f = new File("files/" + name);
            byte[] content = (byte[]) is.readObject();
            Files.write(f.toPath(), content);
        } catch (IOException e) {
            throw new IOException("receiveFile error");
        } catch (ClassNotFoundException i) {
            throw new ClassCastException("no ideia");
        }
    }

    /**
     * opcao -r
     * partilha o nome ficheiro/mensagem trocada por outro client "user"
     */

    public void shareMessage(String user, ObjectOutputStream out) throws FileNotFoundException, IOException {

        List<String> files = userCat.getAllFiles(user);


        out.writeObject(files.size());
        for (String elem : files) {
            File f = new File(elem);
            if (f.exists() && !f.isDirectory()) {
                Path path = Paths.get(elem);
                List<String> lines = Files.readAllLines(path);
                out.writeObject(lines.get(lines.size() - 1));
            } else {
                out.writeObject("nothing");
            }
        }

    }

    /**
     * opcao -r contact
     * partilhar todas as informacoes entre user e contact
     */

    public void shareContact(String contact, String user, ObjectOutputStream out) throws IOException {


        List<String> alph = new ArrayList<String>();
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
     */

    public void shareFile(String contact, String fileName, String user, ObjectOutputStream out) throws IOException {

        try {

            String temp = "files/" + user + "/" + contact;

            Path path = Paths.get(temp + fileName);
            byte[] data = Files.readAllBytes(path);

            String msg = "-f:" + contact + ":" + path.getFileName();

            out.writeObject(msg);
            out.writeObject(data);
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
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
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
                KeyStore readcert = KeyStore.getInstance("RSA");
                readcert.load(readkeystore, pass.toCharArray());

                // gera uma chave privada
                Key privatekey = readcert.getKey("SIServer", pass.toCharArray());

                // cria cifra apartir do certificado
                Cipher cph = Cipher.getInstance("RSA");
                cph.init(Cipher.UNWRAP_MODE, privatekey);

                FileInputStream cenas = new FileInputStream(aeskey);
                ObjectInputStream maiscenas = new ObjectInputStream(cenas);
                byte[] keyencoded = new byte[16];

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
                FileInputStream readkeystore = new FileInputStream("SIServer.keystore");
                KeyStore readcert = KeyStore.getInstance("RSA");
                readcert.load(readkeystore, pass.toCharArray());

                // obtem certificado
                Certificate cert = readcert.getCertificate("SIServer");

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

    public void addToGroup(String creator, String user, String group) throws IOException {
        groupCat.addUserToGroup(creator, user, group);

    }

    public void removeUserFromGroup(String user, String group) {
        groupCat.removeFromGroup(group, user);
    }
}
