package myWhatsServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


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
	 *
	 */
	

	public void handle(String pedido, String user) {
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
	 * escreve para um ficheiro log.txt na pasta log
	 *
	 */
	
	
	public void receiveMessage(String msg, String senduser, String recvuser) {

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
