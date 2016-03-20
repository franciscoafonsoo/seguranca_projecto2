package myWhatsServer;


import java.io.*;
import java.net.Socket;
import java.util.*;

public class MyWhatsClient {


	public static void main( String[] args ) throws IOException, ClassNotFoundException, BadPwdException {

		// ip and port
		String[] server = args[1].split(":");
		String IP = server[0];
		int port = Integer.parseInt(server[1]);

        // filling argv array
		List<String> argv = new ArrayList<>();
        Collections.addAll(argv, args);

		// sockets and in/out streams
        Socket s = new Socket(IP, port);
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());

		// users and passwords. handle commands in the end
        String user = args[0];
        if (args[2].equals("-p")) {

            //prepare user and pass
        	String passwd = args[3];
        	user = user.concat(":");
        	user = user.concat(passwd);

            // write and read
        	out.writeObject(user);
        	String ack = (String) in.readObject();
        	if (ack.equals("NOK")) throw new BadPwdException("wrong password!");

            // remover os argumentos já usados para poder correr o handle (4)
            for(int i=0; i<3; i++)
                argv.remove(0);
            //handle(argv, out);
        }
        
        else {
            Scanner scan = new Scanner(System.in);
        	out.writeObject(user);
        	System.out.println("Enter password: ");
        	String passwd = scan.next();
        	out.writeObject(passwd);
        	String ack = (String) in.readObject();
        	if (ack.equals("NOK")) throw new BadPwdException("wrong password!");

            // remover os argumentos já usados para poder correr o handle (2)
			argv.remove(1);argv.remove(1);
        	//handle(argv, out);
        	scan.close();
        }

        MyWhatsStub.handle(argv, in, out);

        // apartir deste ponto, deve-se escrever no stub.
        // passar la para handle e os metodos para as varias opcoes

		// temp code
    }
}
