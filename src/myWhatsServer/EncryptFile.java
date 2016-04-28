package myWhatsServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;


public class EncryptFile {

	private Key key;
	
	public EncryptFile(Key key) {
		this.key = key;
	}

	public void encryptFile(File file, File f) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher c = Cipher.getInstance("AES");
        //como a cifra vai cifrar o ficheiro, o primeiro parametro tem de ser encrypt mode
        c.init(Cipher.ENCRYPT_MODE, key);
        FileOutputStream fos = new FileOutputStream(f);
        CipherOutputStream cos = new CipherOutputStream(fos, c);
        byte[] escrever = new byte[1024];
        FileInputStream fis = new FileInputStream(file);
        int count;
        while ((count = fis.read(escrever))!=-1) {
        	cos.write(escrever);
        }
        fis.close();
        fos.close();
        cos.close();
        
    }
	
	public void encryptFile(byte[] escrever, File f) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		Cipher c = Cipher.getInstance("AES");
        //como a cifra vai cifrar o ficheiro, o primeiro parametro tem de ser encrypt mode
        c.init(Cipher.ENCRYPT_MODE, key);
        FileOutputStream fos = new FileOutputStream(f);
        CipherOutputStream cos = new CipherOutputStream(fos, c);
        cos.write(escrever);
        cos.close();
	}
	
	public File decryptFile(File f) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
        //como a cifra vai cifrar o ficheiro, o primeiro parametro tem de ser encrypt mode
        c.init(Cipher.DECRYPT_MODE, key);
        File tempFile = new File("temporary_files/tempfile.pdf");
        FileInputStream fis = new FileInputStream(f);
        FileOutputStream fos = new FileOutputStream(tempFile);
        CipherInputStream cis = new CipherInputStream(fis, c);
        byte[] byteArray = new byte[1024];
        int i = cis.read(byteArray);
        fos.write(byteArray);
        System.out.println("i=" +i);
        while (i!= -1) {
        	System.out.println("i = " +i);
        	i=cis.read(byteArray);
        	fos.write(byteArray);
		}
        cis.close();
        fis.close();
        fos.close();
        return tempFile;
	}
	
}
