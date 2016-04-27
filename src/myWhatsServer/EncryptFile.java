package myWhatsServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by sherby on 27-04-2016.
 */
public class EncryptFile {

	private Key key;
	
	public EncryptFile(Key key) {
		this.key = key;
	}

	public void encryptFile(byte[] escrever, File f) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher c = Cipher.getInstance("AES");
        //como a cifra vai cifrar o ficheiro, o primeiro parametro tem de ser encrypt mode
        c.init(Cipher.ENCRYPT_MODE, key);
        FileOutputStream fos = new FileOutputStream(f);
        CipherOutputStream cos = new CipherOutputStream(fos, c);

        cos.write(escrever);
        cos.close();
        fos.close();
    }
	
	public String decryptFile(File f) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
        //como a cifra vai cifrar o ficheiro, o primeiro parametro tem de ser encrypt mode
        c.init(Cipher.DECRYPT_MODE, key);
        FileInputStream fis = new FileInputStream(f);
        CipherInputStream cis = new CipherInputStream(fis, c);
        byte[] byteArray = new byte[1024];
        int i = cis.read(byteArray);
        String s = new String(byteArray);
        while ((i=cis.read(byteArray))!= -1) {
        	s.concat(new String(byteArray));
		}
        cis.close();
        fis.close();
        return s;
	}
	
}
