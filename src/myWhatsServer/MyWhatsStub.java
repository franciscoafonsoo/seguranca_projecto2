package myWhatsServer;


import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MyWhatsStub {

    /**
     * trata dos argumentos introduzidos pelo utilizador para enviar para o servidor
     *
     * @throws IOException
     * @throws ClassNotFoundException 
     * @throws DirException 
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws SignatureException 
     */

    // TODO mau formato a ser impresso no ficheiro ( check SKEL )
    public static void handle(List<String> lista, ObjectInputStream in, ObjectOutputStream out, Certificate own, PrivateKey privateKey) throws IOException, ClassNotFoundException, DirException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, SignatureException {

        String msg;

        
        dir("client");
        
        System.out.println("dentro do handle");
        
        if (!(lista.size() == 0)) {
            switch (lista.get(0)) {
                case "-r":
                    // all
                    if (lista.size() == 1)
                        msg = "-r";
                        // contact
                    else if (lista.size() == 2)
                        msg = lista.get(0) + ":" + lista.get(1);
                        // contact file
                    else
                        msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);
                    out.writeObject(msg);

                    int count;
                    
                    //receber a assinatura e mais magia ***
                    
                    byte[] signatureReceived = (byte[]) in.readObject();
                    Signature sig = Signature.getInstance("SHA256withRSA");
                    X509Certificate certificado = (X509Certificate) in.readObject();
                    sig.initVerify(certificado);
                    
                    
                    
                    
                    byte[] keyciphered = (byte[]) in.readObject();
                    byte[] file = (byte[]) in.readObject();
                    Cipher c = Cipher.getInstance("RSA");
                    c.init(Cipher.UNWRAP_MODE, privateKey);
                    System.out.println(keyciphered.length);
                    Key chave = c.unwrap(keyciphered, "AES", Cipher.SECRET_KEY);
                    File original = new File("client/a.pdf");
                    FileOutputStream fichOS = new FileOutputStream(original);
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, chave);
                    CipherOutputStream cos = new CipherOutputStream(fichOS, cipher);
                    File ficheir = new File("client/a.txt");
                    Files.write(ficheir.toPath(), file);
                    FileInputStream fichIS = new FileInputStream(ficheir);
                    int count2;
                    byte[] bufferFile = new byte[1024];
                    while((count2=fichIS.read(bufferFile))!=-1) {
                    	cos.write(bufferFile);
                    }
                    
                    cos.close();
                    fichOS.close();
                    
                    sig.update(Files.readAllBytes(Paths.get("client/a.pdf")));
                    if (!sig.verify(signatureReceived)) {
                    	System.out.println("assinatura falhada");
                    }
                    
                    
                    break;
                case "-f":
                	
                	Key key = createkey();
                	EncryptFile encrypter = new EncryptFile(key);
                	File originalFile = new File(lista.get(2));
                	FileInputStream fis2 = new FileInputStream(originalFile);
                	File cipheredFile = new File("client/cifrado.txt");
                	encrypter.encryptFile(originalFile, cipheredFile);
                	FileInputStream fis = new FileInputStream(cipheredFile);
                    byte[] readbytes = new byte[1024];
                    

                    Path path = Paths.get("client/cifrado.txt");
                    byte[] data = Files.readAllBytes(path);

                    msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);
                    out.writeObject(msg);
                    
                    

                          

                    Signature s = Signature.getInstance("SHA256withRSA");
                    s.initSign(privateKey);
                    Path pathsign = Paths.get(originalFile.getCanonicalPath());
                    byte[] datasign = Files.readAllBytes(pathsign);
                    s.update(datasign);
                    byte[] signature = s.sign();
                    
                    out.writeObject(signature);
                    
                    out.writeObject(data);
                    /*int size;
                    while ((size = fis.read(readbytes))!= -1) {
                    	System.out.println(size);
                        out.write(readbytes, 0, size);
                    }*/
                    out.flush();
                    System.out.println("acabei de escrever");
                    X509Certificate cert = (X509Certificate) in.readObject();
                    Cipher ciph = Cipher.getInstance("RSA");
                    ciph.init(Cipher.WRAP_MODE, cert);
                    out.write(ciph.wrap(key));
                    Cipher c1 = Cipher.getInstance("RSA");
                    c1.init(Cipher.WRAP_MODE,own);
                    out.write(c1.wrap(key));
                    
                    
                    
                    

                    
                    
                    break;
                case "-m":
                case "-a":
                case "-d":
                    msg = lista.get(0) + ":" + lista.get(1) + ":" + lista.get(2);
                    System.out.println(msg);
                    out.writeObject(msg);

                    // resposta do server

                    // in.readObject(cenas);

                    // check -r or -a or -d
                    // print (-r: List<String>.split("\")

                    break;
                default:
                    throw new IOException("opcao errada");
            }
        } else {
            out.writeObject("Nothing");
        }
        
        
    }
    public static void dir(String name) throws DirException {
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
    
    public static Key createkey() throws NoSuchAlgorithmException {
//    	SecureRandom random = new SecureRandom();
//    	String pass = new BigInteger(130, random).toString(32);
//        Key key = null;
//        try {
//            File aeskey = new File("aeskey.key");
//
//            if (aeskey.exists() && !aeskey.isDirectory()) {
//                // load do keystore
//                FileInputStream readkeystore = new FileInputStream("pedro.keystore");
//                KeyStore readcert = KeyStore.getInstance("JKS");
//                readcert.load(readkeystore, "pedroneves".toCharArray());
//
//                // gera uma chave privada
//                Key privatekey = readcert.getKey("pedro", "pedroneves".toCharArray());
//
//                System.out.println(privatekey);
//                // cria cifra apartir do certificado
//                Cipher cph = Cipher.getInstance("RSA");
//                cph.init(Cipher.UNWRAP_MODE, privatekey);
//
//                FileInputStream cenas = new FileInputStream(aeskey);
//                ObjectInputStream maiscenas = new ObjectInputStream(cenas);
//                //TODO ler o tamanho do ficheiro
//                byte[] keyencoded = new byte[256];
//
//                //noinspection ResultOfMethodCallIgnored
//                maiscenas.read(keyencoded);
//
//                System.out.println("chave: "+ keyencoded);
//                // cria uma chave
//                key = cph.unwrap(keyencoded, "AES", Cipher.SECRET_KEY);
//
//            } else {
//                // gera uma chave AES
//                KeyGenerator kg = KeyGenerator.getInstance("AES");
//                kg.init(128);
//
//                // gera uma chave secreta
//                key = kg.generateKey();
//
//                // load do keystore
//                FileInputStream readkeystore = new FileInputStream("SIClient.keystore");
//                KeyStore readcert = KeyStore.getInstance("JKS");
//                readcert.load(readkeystore, pass.toCharArray());
//
//                // obtem certificado
//                Certificate cert = readcert.getCertificate("SIClient");
//
//                // cria cifra apartir do certificado
//                Cipher cph = Cipher.getInstance("RSA");
//                cph.init(Cipher.WRAP_MODE, cert);
//
//                // escrever a chave ja encriptada po ficheiro
//                FileOutputStream writeaes = new FileOutputStream(aeskey);
//                ObjectOutputStream cenas = new ObjectOutputStream(writeaes);
//                byte[] keyencoded = cph.wrap(key);
//                cenas.write(keyencoded);
//                cenas.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        }
//        return key;
    	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    	SecureRandom random = new SecureRandom(); // cryptograph. secure random 
    	keyGen.init(random); 
    	SecretKey secretKey = keyGen.generateKey();
    	return secretKey;
    }
    
}
