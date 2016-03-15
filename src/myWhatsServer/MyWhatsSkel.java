package myWhatsServer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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

	public void handle(String pedido, String user) throws IOException {
		String[] request = pedido.split(":");
		String op = request[0];
		switch (op) {
		case "-m":
			receiveMessage(request[2], user, request[1]);
			break;
		case "-f":
			
		case "-r":
			if (request.length == 1) {
				shareMessage(user);
			}
			else if (request.length == 2) {
				shareContact(request[1], user);
			}
			else {
				shareFile(request[1], request[2], user);
			}
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

	public void receiveFile(ObjectInputStream is, String senduser, String recvuser, String name, byte[] barray) throws IOException {

		// filename apartir de File f.getName()

		// COMPLETAR E VERIFICAR

		try {

			FileOutputStream fos = new FileOutputStream(name);

			BufferedOutputStream bos = new BufferedOutputStream(fos);

			int bytesRead = is.read(barray, 0, barray.length);
			bos.write(barray, 0, bytesRead);
			bos.close();
		}
		catch (IOException e){
			throw new IOException("receiveFile error");
		}

	}

	/**
	 * opcao -r
	 * partilha o nome ficheiro/mensagem trocada por outro client "user"
	 *
	 */

	public void shareMessage(String user) {

	}

	/**
	 * opcao -r contact
	 * partilhar todas as informacoes entre user e contact
	 *
	 */

	public void shareContact(String contact, String user) {

	}
	
	
	/**
	 * opcao -r contact file
	 * enviar o ficheiro com nome fileName, enviado por contact
	 *
	 */
	
	public void shareFile(String contact, String fileName, String user) {
		
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
