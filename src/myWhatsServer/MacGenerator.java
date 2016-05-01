package myWhatsServer;


import java.io.*;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class MacGenerator {

    private byte[] pwd;

    //eu a bater com a cabeca na parede por causa de java. f-u-c-k java

    // ignore, tou a deixar aqui só pa se rirem. no espaco de 1h, ja mudei esta classe de
    // singleton, para ter dois construtores, um com password outro sem...
    private boolean trigger = false;

    private static MacGenerator INSTANCE = null;

    
    private MacGenerator() {
	}

    public static MacGenerator getInstance() {
    	if (INSTANCE==null) {
    		INSTANCE = new MacGenerator();
    	}
    	return INSTANCE;
    }
    
    
    public void setPassword(String pass) {
        this.pwd = pass.getBytes();
    }


    private String generateMac(File f) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        try {
            // get a key for the HMAC-SHA256 keyed-hashing algorithm using the password given by the method caller
        	
        	SecretKeySpec key = new SecretKeySpec(pwd, "HmacSHA256");
            
            // create a MAC and initialize with the above key
            Mac mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);

            Path path = Paths.get(f.getAbsolutePath());
            byte[] data = Files.readAllBytes(path);
            
            mac.update(data);
            // create a digest from the byte array
            byte[] digest = mac.doFinal();

            String r = new String(digest);

            return r;

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
        return "";
    }

    public boolean createMac(File f, String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException {

        Path p = Paths.get(filename);
        File a = new File(filename);
        
        try {
        	if(a.exists())
        		Files.delete(p);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", p);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", p);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

        File g = new File(filename);

        if (!g.isDirectory())
            try (PrintWriter output = new PrintWriter(new FileWriter(g, true))) {
                if (!generateMac(f).equals(""))
                    output.print(generateMac(f));
                output.flush();
                output.close();
                return true;
            } catch (IOException e) {
                throw new IOException("mac error");
            }

        return false;
    }

    public boolean checkMac(File f, File mac) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, FileNotFoundException {

        String previous = null;

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(mac.getAbsolutePath()));
            previous = new String(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return generateMac(f).equals(previous);
    }

}
