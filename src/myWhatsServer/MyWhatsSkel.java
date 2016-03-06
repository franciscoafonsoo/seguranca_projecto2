package myWhatsServer;

/**
 * Created by sherby on 06-03-2016.
 */
public class MyWhatsSkel {

	public MyWhatsSkel {

	}

	/**
	 * autenticar um cliente "user"
	 *
	 */

	public void login(String user, String pass) {

	}

	/**
	 * registar um client "user"
	 *
	 */

	public void register(String user, String pass) {

	}

	/**
	 * opcao -m
	 * recebe mensagem e a autorizacao de acesso ao client "user"
	 *
	 */

	public void receiveMessage(String msg, String senduser, String recvuser) {

	}

	/**
	 * opcao -f
	 *
	 * recebe um ficheiro no servidor e da autorizacao de acesso ao client "user"
	 *
	 */

	public void receiveFile(File f, String senduser, String recvuser) {

	}

	/**
	 * opcao -r 
	 *
	 * partilha o nome ficheiro/mensagem trocada por outro client "user"
	 *
	 */

	public void shareMessage(String msg, String user) {

	}

	/**
	 * opcao -r file
	 *
	 * partilhar um ficheiro trocado por outro client "user"
	 *
	 */

	public File shareFile(File f, String user) {

	}
}
