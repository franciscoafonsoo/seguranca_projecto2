package myWhatsServer;


import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class MyWhatsClient {


    public static void main(String[] args) throws IOException, ClassNotFoundException, BadPwdException, DirException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, KeyStoreException, CertificateException, UnrecoverableKeyException, SignatureException {

        // ip and port
        String[] server = args[1].split(":");
        String IP = server[0];
        int port = Integer.parseInt(server[1]);

        // filling argv array
        List<String> argv = new ArrayList<>();
        Collections.addAll(argv, args);

        // sockets and in/out streams
        System.setProperty("javax.net.ssl.trustStore", "SIClient.keystore");
        SocketFactory sf = SSLSocketFactory.getDefault();
        Socket s = sf.createSocket(IP, port);
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(new File("tiago.keystore")), "tiagocalha".toCharArray());

        // users and passwords. handle commands in the end
        String user = args[0];
        System.out.println("inicio");
        if (args[2].equals("-p")) {

            //prepare user and pass
            String passwd = args[3];
            user = user.concat(":");
            user = user.concat(passwd);

            // write and read
            System.out.println("write");
            out.writeObject(user);
            System.out.println("read");
            String ack = (String) in.readObject();
            System.out.println(ack);
            if (ack.equals("NOK")) throw new BadPwdException("wrong password!");

            // remover os argumentos já usados para poder correr o handle (4)
            for (int i = 0; i < 4; i++)
                argv.remove(0);
            
            //handle(argv, out);
        } else {
            Scanner scan = new Scanner(System.in);
            out.writeObject(user);
            System.out.println("Enter password: ");
            String passwd = scan.next();
            out.writeObject(passwd);
            String ack = (String) in.readObject();
            if (ack.equals("NOK")) throw new BadPwdException("wrong password!");

            // remover os argumentos já usados para poder correr o handle (2)
            argv.remove(1);
            argv.remove(1);
            
            //handle(argv, out);
            scan.close();
        }

        System.out.println("handle");
        MyWhatsStub.handle(argv, in, out, ks.getCertificate("tiago"), ks.getKey("tiago", "tiagocalha".toCharArray()));
        System.out.println("fora do handle");

        if (!(argv.size() == 0)) {
            System.out.println(argv.get(0));
//            if (!(argv.get(0).equals("-r"))) {
//                int n;
//                System.out.println("antes do while");
//                int size = (Integer) in.readObject();
//                for (int i = 0; i < size; i++) {
//                    System.out.println("receber");
//                    String message = (String) in.readObject();
//                    if (!(message.equals("nothing"))) {
//                        System.out.println("imprimir");
//                        String[] mensagem = message.split("/");
//                        System.out.print("Contact: " + mensagem[0] + "\n");
//                        System.out.print("me: " + mensagem[1] + "\n");
//                        System.out.print("Data: " + mensagem[2] + "\n");
//                    }
//                }
//            }
//            if (argv.get(0).equals("-r")) {
//            	byte[] ficheiro = (byte[]) in.readObject();
//            	File f = new File("client/a.pdf");
//            	FileOutputStream fos = new FileOutputStream(f);
//            	fos.write(ficheiro);
//            }
        }
        out.close();
        in.close();
        s.close();

        // apartir deste ponto, deve-se escrever no stub.
        // passar la para handle e os metodos para as varias opcoes
    }
}
