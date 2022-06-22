import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.Charset;


/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup{
	public static void main(String[] args) {
		
		//args = new String[]{"-N", "myBook"};
		SecretKey key = null;
		try {
			key = generateAESKey();
		} catch (NoSuchAlgorithmException e) {
			exit();
		}

		if (args.length < 2) {
			System.out.println("Usage: setup <logfile pathname>");
			exit();
		}

		if (args[0].equals("-N")) {
			Pattern alphaNum = Pattern.compile("^[a-zA-Z0-9._]+$"); 
			Matcher matcher = alphaNum.matcher(args[1]);
			if (matcher.find()) {
				File file = new File(args[1]);
				if (file.exists()) {
					exit();
				}
				Gradebook book = new Gradebook(args[1], key);
				//saveBook(book, args[1], convertStringToHex(Base64.getEncoder().encodeToString(key.getEncoded())));
				saveBook(book, args[1], convertStringToHex(Base64.getEncoder().encodeToString(key.getEncoded())));

			} else {
				exit();
			}

		} else {
			exit();
		}

		return;
	}

	private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(256); // The AES key size in number of bits
		return generator.generateKey();
	  }

	private static String convertStringToHex(String str) {
    
		char ch[] = str.toCharArray();
		StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < ch.length; i++) {
        	sb.append(Integer.toHexString((int) ch[i]));
    	}
    	return sb.toString();
	}

	private static void saveBook(Gradebook book, String fileName, String key) {
		Charset UTF_8 = StandardCharsets.UTF_8;
	    String algorithm = "SHA3-256";	
		try {

			if(!book.validateKey(key)){
		

				exit();
			}
	
			try {
			
			String key2 = new String(Base64.getEncoder().encode(key.getBytes(UTF_8)));
			byte[] shaInBytes = setup.digest(key2.getBytes(UTF_8), algorithm);
			String result = new String(Base64.getEncoder().encode(shaInBytes));
			sealObject(book, fromStringToAESkey(result), fileName);
			
			}catch(Exception e) {
			
				exit();
			}
			
		} catch (Exception e) {

			exit();
		}
	}
	 
	
	
	private static void sealObject(Serializable obj, SecretKey key, String path) throws IllegalBlockSizeException, IOException {
		Cipher cipher = null;
		Serializable result = null;
		try {
		cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        SealedObject sealedObject = null;
        sealedObject = new SealedObject(obj, cipher);
        CipherOutputStream cipherOutputStream = null;
        cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(path)), cipher);
        ObjectOutputStream outputStream = null;
        outputStream = new ObjectOutputStream(cipherOutputStream);
        outputStream.writeObject(sealedObject);
        outputStream.close(); 
        return;
        
		}catch(Exception e) {
		
			exit();
		}
	

		exit();
			
	}
	
	private static Gradebook getGradebook(String fileName, String key) {
		Charset UTF_8 = StandardCharsets.UTF_8;
	    String algorithm = "SHA3-256";	
		try {
			
			File file = new File(fileName);

			if (!file.exists()) {
			

				exit();
			} else {
				String key2 = new String(Base64.getEncoder().encode(key.getBytes(UTF_8)));
				byte[] shaInBytes = setup.digest(key2.getBytes(UTF_8), algorithm);
				String result = new String(Base64.getEncoder().encode(shaInBytes));
				
				Serializable bookResult = unsealObject(fromStringToAESkey(result), fileName);
				Gradebook book = (Gradebook) bookResult;
				
				
				if(book == null || !(book.validateKey(key))){
		

					exit();
				}    

				return book;
			}
		} catch (Exception e) {
		

			exit();
		}

		return null;
	}
	
	private static Serializable unsealObject(SecretKey key, String fileName) {
		Cipher cipher = null;
        Serializable userList = null;
        try {
        cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

        //Code to write your object to file

        cipher.init(Cipher.DECRYPT_MODE, key);         
        CipherInputStream cipherInputStream = null;
        cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(fileName)), cipher);

        ObjectInputStream inputStream = null;
        inputStream = new ObjectInputStream(cipherInputStream);
        SealedObject sealedObject = null;
        sealedObject = (SealedObject) inputStream.readObject();
        userList = (Serializable) sealedObject.getObject(cipher);  
        return userList;
        
        
        }catch(Exception e) {
		
        	exit();
        }
        
        return null;
	}

	private static SecretKey fromStringToAESkey(String s) {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(s);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		return originalKey;
	}
	
	private static byte[] digest(byte[] input, String algorithm) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    

	private static void exit() {
		System.out.println("Invalid");
		System.exit(255);
	}
}
