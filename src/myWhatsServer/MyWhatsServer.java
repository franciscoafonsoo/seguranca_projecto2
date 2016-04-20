package myWhatsServer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;


public class MyWhatsServer {

    public static void main(String[] args) throws NumberFormatException, IOException, DirException {
        System.out.println("servidor: main");
        MyWhatsServer server = new MyWhatsServer();
        server.startServer(Integer.parseInt(args[0]));
    }

    @SuppressWarnings("resource")
    private void startServer(int port) throws IOException, DirException {
        ServerSocket sSoc = null;
        MyWhatsSkel skel = new MyWhatsSkel();

        try {
            System.setProperty("javax.net.ssl.keyStore", "SIServer.keystore");
            System.setProperty("javax.net.ssl.keyStorePassword", "Fculproject1");
            ServerSocketFactory sf = SSLServerSocketFactory.getDefault();
            sSoc = sf.createServerSocket(port);

            // antiga ligação
            //sSoc = new ServerSocket(port);

            skel.rmdir("log");
            skel.rmdir("msg");
            skel.rmdir("groups");
            skel.rmdir("files");

            skel.dir("log");
            skel.dir("msg");
            skel.dir("groups");
            skel.dir("files");


        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (DirException e) {
            throw new DirException("wrong dir");
        }

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Socket inSoc = sSoc.accept();
                ServerThread newServerThread = new ServerThread(inSoc, skel);
                newServerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ServerThread extends Thread {

        private Socket socket = null;
        private MyWhatsSkel skel;

        ServerThread(Socket inSoc, MyWhatsSkel skel) {
            socket = inSoc;
            this.skel = skel;
            System.out.println("nova thread iniciada");
        }

        public void run() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                String auth = (String) in.readObject();
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                String[] data = auth.split(":");
                String user;
                String pwd;
                
                if (data.length == 1) {
                    user = data[0];
                    pwd = (String) in.readObject();
                } else {
                    user = data[0];
                    pwd = data[1];
                }
                
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
                

                if (skel.login(user, pwd, salt).equals("NOK"))
                    out.writeObject("NOK");
                else {
                    out.writeObject("OK");
                    System.out.println("receber mensagem");
                    String pedido = (String) in.readObject();
                    System.out.println(pedido);
                    System.out.println("entrar no skel");
                    if (!(pedido.equals("Nothing"))) {
                        skel.handle(pedido, user, in, out);
                    }
                }

                out.close();
                in.close();

                socket.close();

            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}