package myWhatsServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

import javax.crypto.NoSuchPaddingException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;


public class MyWhatsServer {

    public static void main(String[] args) throws NumberFormatException, IOException, DirException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("servidor: main");
        MyWhatsServer server = new MyWhatsServer();
        server.startServer(Integer.parseInt(args[0]), args[1]);
    }

    @SuppressWarnings("resource")
    private void startServer(int port, String pass) throws IOException, DirException, InvalidKeyException, NoSuchAlgorithmException {
        ServerSocket sSoc = null;
        MyWhatsSkel skel = new MyWhatsSkel(pass);

        Console cnsl = null;
        char[] passwd = cnsl.readPassword("Password MAC: ");
        MacGenerator mac = new MacGenerator();
        mac.setPassword(passwd);
        File f = new File("logs/passwords.txt");
        String filemac = "mac/passwords.txt";
        File g = new File(filemac);


        // verificacoes do mac do ficheiro de passwords.
        if (g.exists()) {
            if (!mac.checkMac(f, g)) {
                System.out.print("comparação de mac falhou. exiting...");
                System.exit(1);
            } else {
                int i = 0;
                while (i != 1 && i != 2) {
                    System.out.print("Criar novo MAC(1) ou Sair(2)?");
                    Scanner scn = new Scanner(System.in);
                    i = scn.nextInt();
                    if (i == 1)
                        mac.createMac(f, filemac);
                    if (i == 2) {
                        scn.close();
                        System.exit(1);
                    }
                }
            }
        }
        try {
            System.setProperty("javax.net.ssl.keyStore", "SIServer.keystore");
            System.setProperty("javax.net.ssl.keyStorePassword", pass);
            ServerSocketFactory sf = SSLServerSocketFactory.getDefault();
            sSoc = sf.createServerSocket(port);
            // criar os directorios

            skel.rmdir("log");
            skel.rmdir("msg");
            skel.rmdir("groups");
            skel.rmdir("files");
            skel.rmdir("mac");
            skel.rmdir("temporary_files");

            skel.dir("log");
            skel.dir("msg");
            skel.dir("groups");
            skel.dir("files");
            skel.dir("mac");
            skel.dir("temporary_files");

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

                if (skel.login(user, pwd).equals("NOK"))
                    out.writeObject("NOK");
                else {
                    out.writeObject("OK");
                    System.out.println("receber mensagem");
                    String pedido = (String) in.readObject();
                    System.out.println(pedido);
                    System.out.println("entrar no skel");
                    if (!(pedido.equals("Nothing"))) skel.handle(pedido, user, in, out);
                }

                out.close();
                in.close();
                socket.close();

            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
    }
}