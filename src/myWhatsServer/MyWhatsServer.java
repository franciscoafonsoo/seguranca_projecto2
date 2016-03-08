package myWhatsServer;

import java.awt.BufferCapabilities;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MyWhatsServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");
		MyWhatsServer server = new MyWhatsServer();
		server.startServer(Integer.parseInt(args[1]));
	}

	public void startServer (int port){
		ServerSocket sSoc = null;
		MyWhatsSkel skel = new MyWhatsSkel();
        
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
         
		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc, skel);
				newServerThread.start();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		}
		//sSoc.close();
	}


	class ServerThread extends Thread {

		private Socket socket = null;
		private MyWhatsSkel skel;
		ServerThread(Socket inSoc, MyWhatsSkel skel) {
			socket = inSoc;
			this.skel = skel;
			System.out.println("thread do server para cada cliente");
		}
 
		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				File log = new File("log");

				if(!(log.exists() && log.isDirectory())) {
					boolean feito = log.mkdirs();
					if (feito)
						System.out.println("dir created");
					else
						System.out.println("erro");
				}
				else
					System.out.println("já existe");

				File f = new File("log/users.txt");
				String auth = (String) inStream.readObject();
				String[] data = auth.split(":");
				String user;
				String pwd;
				if (data.length == 1) {
					user = data[0];
					pwd = (String) inStream.readObject();
				}
				else {
					user = data[0];
					pwd = data[1];
				}

				if (skel.login(user, pwd).equals("NOK"))
					outStream.writeObject("NOK");
				else {
					String pedido = (String) inStream.readObject();
					skel.handle(pedido, user);
				}
				
//				int n;
//			
//				try {
//					n = (Integer) inStream.readObject();
//					System.out.println("thread: depois de receber a dimensao");
//				}catch (ClassNotFoundException e1) {
//					e1.printStackTrace();
//				}

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}