package myWhatsServer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MacGenerator {
	
	private String pwd;
	
	public MacGenerator(String pwd) {
		this.pwd = pwd;
	}
	
	public String generateMac(File f) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		try {
            // get a key for the HMAC-SHA256 keyed-hashing algorithm using the password given by the method caller
			SecretKey key = new SecretKeySpec(pwd.getBytes(), "HmacSHA256");
            
            // create a MAC and initialize with the above key
            Mac mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);

            Path path = Paths.get(f.getAbsolutePath());
            byte[] data = Files.readAllBytes(path);
            
            mac.update(data);
            // create a digest from the byte array
            byte[] digest = mac.doFinal();
            return new String(digest);

        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("catalog - register");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("catalog - register");
        } catch (InvalidKeyException e) {
            try {
				throw new InvalidKeyException("catalog - register");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
