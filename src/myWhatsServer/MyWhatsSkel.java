package myWhatsServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
	 * recebe mensagem e a autorizacao de acesso ao client "user"
	 * escreve para um ficheiro recvuser.txt na pasta msg
	 *
	 */
	
	
	public void receiveMessage(String msg, String senduser, String recvuser) throws IOException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();

		File log = new File("msg");

		if(!(log.exists() && log.isDirectory())) {
			boolean feito = log.mkdir();
			if (feito)
				System.out.println("dir created");
			else
				System.out.println("erro");
		}
		else
			System.out.println("já existe");

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

	public void receiveFile(File f, String senduser, String recvuser) {

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
