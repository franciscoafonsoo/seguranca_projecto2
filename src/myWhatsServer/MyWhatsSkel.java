package myWhatsServer;


import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MyWhatsSkel {

	/**
	 * instancias
	 *
	 */

	private UserCatalog userCat;

	/**
	 * construtor
	 *
	 */
	
	public MyWhatsSkel() {
		userCat = UserCatalog.getInstance();
	}

	/**
	 * autenticar um cliente "user"
	 * @throws IOException 
	 *
	 */

	public String login(String user, String pwd) throws IOException{

		if(userCat.login(user, pwd)) {
			return "OK";
		}
		else {
			return "NOK";
		}
	}

	/**
	 * registar um client "user"
	 * @throws IOException 
	 *
	 */

	public void handle(String pedido, String user, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		String[] request = pedido.split(":");
		String op = request[0];
		switch (op) {
		case "-m":
			receiveMessage(request[2], user, request[1]);
			break;
		case "-f":
			receiveMessage(request[2], user, request[1]);
            receiveFile(request[2], in);
			break;
		case "-r":
			if (request.length == 1) {
				shareMessage(user, out);
			}
			else if (request.length == 2) {
				shareContact(request[1], user, out);
			}
			else {
				shareFile(request[1], request[2], user, out);
			}
            break;
        }

	}

	
	/**
	 * opcao -m
	 * recebe mensagem e nome do ficheiro partilhado e a autorizacao de acesso ao client "user"
	 * escreve para um ficheiro recvuser.txt na pasta msg
	 *
	 */
	
	
	public void receiveMessage(String msg, String senduser, String recvuser) throws IOException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();

		String dt = dateFormat.format(cal.getTime());

		File f = new File("msg/" + recvuser + ".txt");
		if(f.exists() && !f.isDirectory()) {
			try(PrintWriter output = new PrintWriter(new FileWriter(f,true)))
			{
				output.printf("%s", "Contact :"  + senduser + "/");
				output.printf("%s", dt + "/");
				output.printf("%s\r\n", msg);
			}
			catch (IOException e) {
				throw new IOException("receiveMessage error");
			}
		}
		else {
			try(PrintStream output = new PrintStream(f)){
				output.printf("%s", "Contact :"  + senduser + "/");
				output.printf("%s", dt + "/");
				output.printf("%s\r\n", msg);
			}
			catch (IOException e) {
				throw new IOException("receiveMessage error");
			}
		}
	}

	/**
	 * opcao -f
	 * recebe um ficheiro no servidor e da autorizacao de acesso ao client "user"
	 *
	 */

	public void receiveFile(String name, ObjectInputStream is) throws IOException {

		try {
            File f = new File("files/" + name);
            byte[] content = (byte[]) is.readObject();
            Files.write(f.toPath(), content);
		}
		catch (IOException e){
			throw new IOException("receiveFile error");
		}
        catch (ClassNotFoundException i){
            throw new ClassCastException("no ideia");
        }
	}

	/**
	 * opcao -r
	 * partilha o nome ficheiro/mensagem trocada por outro client "user"
	 *
	 */

	public void shareMessage(String user, ObjectOutputStream out) throws FileNotFoundException, IOException {

        Path path = Paths.get("msg/" + user);
        List<String> lines = Files.readAllLines(path);

        out.writeObject(lines);
	}

	/**
	 * opcao -r contact
	 * partilhar todas as informacoes entre user e contact
	 *
	 */

	public void shareContact(String contact, String user, ObjectOutputStream out) {

        //HALP.

	}
	
	
	/**
	 * opcao -r contact file
	 * enviar o ficheiro com nome fileName, enviado por contact
	 *
	 */
	
	public void shareFile(String contact, String fileName, String user, ObjectOutputStream out) throws IOException {

        try {
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);

            String msg = "-f:" + contact + ":" + path.getFileName();

            out.writeObject(msg);
            out.writeObject(data);
        }
        catch (IOException e){
            throw new IOException("receiveFile error");
        }
	}

	/**
	 * check and make dirs if not yet created
	 */

	public void dir(String name) throws DirException {
		File log = new File(name);

		if(!(log.exists() && log.isDirectory())) {
			boolean feito = log.mkdirs();
			if (feito) {
				System.out.println(name + " CREATED");
			}
			else{
				throw new DirException("verificar permissoes, etc.");
			}
		}
		else
			System.out.println(name + " OK");
	}
}
