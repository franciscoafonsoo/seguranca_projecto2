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
        
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
         
		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
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

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}
 
		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				File f = new File("users.txt");
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
				
				
				Boolean found = false;
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(f));
				while ((line=reader.readLine()) != null || !(found)) {
					String[] dataF = line.split(":");
					if (dataF[0] == user) {
						if (dataF[1]==pwd) {
							found = true;
						}
						else {
							outStream.writeObject("NOK");
						}
					}
					//registar o user
					
				}
				
				//escrever no ficheiro caso user nao exista
				FileWriter writer = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(writer);
				bw.write(user+":"+pwd);
				
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