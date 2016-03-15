package myWhatsServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class MyWhats {

	
	public static void main( String[] args ) throws UnknownHostException, IOException, ClassNotFoundException, BadPwdException
    {
		String[] server = args[2].split(":");
		String IP = server[0];
		int port = Integer.parseInt(server[1]);
		List<String> argv = null;
		for (String e : args) {
			argv.add(e);
		}
        Socket s = new Socket(IP, port);
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        String user = args[1];
        byte[] buf = new byte[32];
        if (args[3].equals("-p")) {
        	String passwd = args[4];
        	user = user.concat(":");
        	user = user.concat(passwd);
        	out.writeObject(user);
        	String ack = (String) in.readObject();
        	if (ack.equals("NOK")) throw new BadPwdException("wrong password!");
            argv.remove(1); argv.remove(1); argv.remove(1); argv.remove(1);
            handle(argv, out);
        }
        
        else {
            Scanner scan = new Scanner(System.in);
        	out.writeObject(user);
        	System.out.println("Enter password: ");
        	String passwd = scan.next();
        	out.writeObject(passwd);
        	String ack = (String) in.readObject();
        	if (ack.equals("NOK")) throw new BadPwdException("wrong password!");
        	argv.remove(1); argv.remove(1);
        	handle(argv, out);
        	scan.close();
        }

        File f = new File("IIO-Exame_2014_01_20.pdf");
        FileInputStream input = new FileInputStream (f);
        out.writeObject(f.length());
        int n;
        
       while ((n = input.read(buf, 0, 32))!=-1) {
    	   out.write(buf, 0, n);
       }
       
       
    }
	
	private static void handle(List<String> lista, ObjectOutputStream out) throws IOException {
		String[] args = (String[]) lista.toArray();
		if (args[0].equals("-r")){

			String mensagem;

			if (args.length==1)
				mensagem="-r";
			else if (args.length==2)
				mensagem = args[0] + ":"+ args[1];
			else
				mensagem = args[0] + ":"+ args[1] +":"+ args[2];
			out.writeObject(mensagem);
		}
		else {
			String mensagem2 = args[0] + ":"+ args[1] +":"+ args[2];
			out.writeObject(mensagem2);
		}
    }

	private void sendFile (ObjectOutputStream out, File f, byte[] barray) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

		// re check this line
		bis.read(barray, 0, barray.length);

		out.write(barray, 0, barray.length);
		out.flush();
	}
}
