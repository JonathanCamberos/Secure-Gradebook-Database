//import ...
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private static void parse_cmdline(String[] args) {

    String[] result = new String[10];

    if(args.length==1)
      System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
    if(args.length>=2) {
    
      if(args.length <= 4) {
    	 
    	  optionError();
      }
    	
      if(args[0].compareTo("-N") != 0 || args[2].compareTo("-K") != 0){
    	 

    	 optionError();
      }

      if(alphaName(args[1]) == false){


        optionError();
      }

      //checks that key is in hex format
      if(hexKey(args[3]) == false){
    
        optionError();
      }
     

      Gradebook currBook = getGradebook(args[1], args[3]);
      if(currBook == null){
    	 

        optionError();
      }
     
      
      //Add Assignment: "-N <book name> -K <key> -AA -AN <name> -P <points> -W <weight> ... (more possible)"
      if(args[4].compareTo("-AA") == 0 ){
        if(args.length < 11){
    
          optionError();
        }
        updateBook(addAssignment(currBook, args), args[3]);
        
      //Delete Assignment: "-N <book name> -K <key> -DA -AN <name> ... (more possible)"
      }else if(args[4].compareTo("-DA") == 0){
        if(args.length < 7){
   

          optionError();
        }
        updateBook(deleteAssignment(currBook, args), args[3]);

      //Add Student: "-N <book name> -K <key> -AS -FN <Student-first-name> -LN <Student-Last-name> ... more
      }else if(args[4].compareTo("-AS") == 0){
        if(args.length < 9){
     
          optionError();
        }
        updateBook(addStudent(currBook, args), args[3]);

      //Delete Student: "-N <book name> -K <key> -DS -FN <Student-first-name> -LN <Student-Last-name> ... more 
      }else if(args[4].compareTo("-DS") == 0){
        if(args.length < 9){
     

          optionError();
        }
        updateBook(deleteStudent(currBook, args), args[3]);

      //Add Grade: "-N <book name> -K <key> -AG -FN <Student-first-name> -LN <Student-Last-name> -AN <Assignment-name> -G <grade>... more 
      }else if(args[4].compareTo("-AG") == 0){
        if(args.length < 13){
      	

          optionError();
        }
        updateBook(addGrade(currBook, args), args[3]);
      }else{
    

         optionError();
      }


    }
   
    return; 
  }

  private static void updateBook(Gradebook updatedBook, String key) {
	  if (!(updatedBook.validateKey(key))) {
    	

			optionError();
		}
        
	  File file = new File(updatedBook.getName(key));
	  
	  if(file.delete() == false){
    	

      optionError();
    } 
        			
	  saveBook(updatedBook, updatedBook.getName(key), key);
  }
  
  
  private static Gradebook addAssignment(Gradebook currBook, String[] args){
    //potential: "-N <book name> -K <key> -AA -AN <name> -P <points> -W <weight> ... (more possible)"
    String[] results = new String[5];
    
    results[0] = args[1]; //name
    results[1] = args[3]; //key

    if (!(currBook.validateKey(results[1]))) {
  	  	

			optionError();
		}
    
    for(int i = 5; i < args.length; i+=2){
      
      if(args[i].compareTo("-AN") == 0){
        results[2] = args[i+1];
      }else if(args[i].compareTo("-P") == 0){
        results[3] = args[i+1];
      }else if(args[i].compareTo("-W") == 0){
        results[4] = args[i+1];
      }else{
    

        optionError(); 
      }
    }

    if(verifyAllProvided(results) == false){
  	

      optionError();
    }

    if(!validate(results[2], 2)){
  	 

      optionError();
    }
    
    double points;
    double weight;
    try{
      points = Double.parseDouble(results[3]);
      if(points < 0){
    	 

        optionError();
      }
      weight = Double.parseDouble(results[4]);
      if((0 <= weight) == false || (weight <= 1) == false) {
    	

        optionError();
      }
      
      if(currBook.addAssignment(results[1], results[2], new Assignment(results[2], points, weight)) == false){
    	 

    	  optionError();
      }
    }catch(Exception e) {
  	 

      optionError();
    }

    return currBook;
  }




  private static Gradebook deleteAssignment(Gradebook currBook, String[] args){
    //potential: "-N <book name> -K <key> -DA -AN <name> ... (more possible)"
    String[] results = new String[3];
    
    results[0] = args[1]; //name
    results[1] = args[3]; //key

    if (!(currBook.validateKey(results[1]))) {
  	 

			optionError();
		}
    
    for(int i = 5; i < args.length; i+=2){
      
      if(args[i].compareTo("-AN") == 0){
        results[2] = args[i+1];
      }else{
    	 

        optionError();
      }
    }

    if(verifyAllProvided(results) == false){
  	

      optionError();
    }

    if(!validate(results[2], 2)){
  

      optionError();
    }

    if(currBook.deleteAssignment(results[1], results[2]) == false){
  	  

      optionError();
    }

    return currBook;

  }

  private static Gradebook addStudent(Gradebook currBook, String[] args){
    //potential: "-N <book name> -K <key> -AS -FN <Student-first-name> -LN <Student-Last-name>
    String[] results = new String[4];
    
    results[0] = args[1]; //name
    results[1] = args[3]; //key

    if (!(currBook.validateKey(results[1]))) {
  

			optionError();
		}
    
    for(int i = 5; i < args.length; i+=2){
      
      if(args[i].compareTo("-FN") == 0){
        results[2] = args[i+1];
      }else if(args[i].compareTo("-LN") == 0){
        results[3] = args[i+1];
      }else{
    

        optionError();
      }
    }
    if(verifyAllProvided(results) == false){
 

      optionError();
    }

    if(!validate(results[2], 3)){
  	

      optionError();
    }

    if(!validate(results[3], 3)){
  	 

      optionError();
    }

    if(currBook.addStudent(results[1], results[2], results[3]) == false){
  

      optionError();
    }

    return currBook;

  }

  private static Gradebook deleteStudent(Gradebook currBook, String[] args){
    //potential: "-N <book name> -K <key> -DS -FN <Student-first-name> -LN <Student-Last-name>
    String[] results = new String[4];
    
    results[0] = args[1]; //name
    results[1] = args[3]; //key

    if (!(currBook.validateKey(results[1]))) {
  	

			optionError();
		}
    
    for(int i = 5; i < args.length; i+=2){
      
      if(args[i].compareTo("-FN") == 0){
        results[2] = args[i+1];
      }else if(args[i].compareTo("-LN") == 0){
        results[3] = args[i+1];
      }else{
   

        optionError();
      }
    }
    if(verifyAllProvided(results) == false){
  

      optionError();
    }

    if(!validate(results[2], 3)){
  

      optionError();
    }

    if(!validate(results[3], 3)){
  	

      optionError();
    }

    

    if(currBook.deleteStudent(results[1], results[2], results[3]) == false){


      optionError();
    }

    return currBook;

  }

  private static Gradebook addGrade(Gradebook currBook, String[] args){
    //potential: "-N <book name> -K <key> -AG -FN <Student-first-name> -LN <Student-Last-name> -AN <Assignment-name> -G <grade>... more
    String[] results = new String[6];
    
    results[0] = args[1]; //name
    results[1] = args[3]; //key

    if (!(currBook.validateKey(results[1]))) {
  

			optionError();
		}
    
    for(int i = 5; i < args.length; i+=2){
      
      if(args[i].compareTo("-FN") == 0){
        results[2] = args[i+1];
      }else if(args[i].compareTo("-LN") == 0){
        results[3] = args[i+1];
      }else if(args[i].compareTo("-AN") == 0){
        results[4] = args[i+1];
      }else if(args[i].compareTo("-G") == 0){
        results[5] = args[i+1];
      }else{
    

        optionError();
      }
    }

    if(verifyAllProvided(results) == false){       
  	

      optionError();
    }

    if(!validate(results[2], 3)){
    	 

      optionError();
    }

    if(!validate(results[3], 3)){
    

      optionError();
    }

    if(!validate(results[4], 2)){
    	

      optionError();
    }

    int grade;
    try{
      grade = Integer.parseInt(results[5]);
      if(grade < 0){
      

        optionError();
      }
      if(currBook.addGrade(results[1], results[2], results[3], results[4], grade) == false){
  

        optionError();
      }



    }catch(Exception e){


      optionError();
    }

    return currBook;
    
  }

  private static boolean verifyAllProvided(String[] results){
    for(String curr : results){
      if(curr == null){
        return false;
      }
    }
    return true;
  }

  private static boolean alphaName(String gradeBookName){
    //verifys alpha-numeric + underscores + periods
    Pattern alphaNum = Pattern.compile("^[a-zA-Z0-9\\s.]+$");
    Matcher matcher = alphaNum.matcher(gradeBookName);
    boolean matchFound = matcher.find();
    return matchFound;
  }

  private static boolean hexKey(String key){
    Pattern hexDec = Pattern.compile("^[0-9a-fA-F]+$");
    Matcher matcher = hexDec.matcher(key);
    boolean matchFound = matcher.find();
    return matchFound;
  }

  private static void optionError(){
    System.out.print("invalid");
    System.exit(225);
  }


  public static void main(String[] args) {
    
    parse_cmdline(args);
    
    return;
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
        System.out.print(e.getMessage());

        	exit();
        }
        
        return null;
	}

public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
}

  public static SecretKey fromStringToAESkey(String s) {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(s);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		return originalKey;
	}

  private static boolean validate(String input, int type) {
		boolean value = false;
		if(type == 1) {
			Pattern alphaNum = Pattern.compile("^[a-zA-Z0-9._]+$");
		    Matcher matcher = alphaNum.matcher(input);
		    value = matcher.find();
		} else if (type == 2) {
			Pattern alphaNum = Pattern.compile("^[a-zA-Z0-9]+$");
		    Matcher matcher = alphaNum.matcher(input);
		    value = matcher.find();
		} else {
			Pattern alphaNum = Pattern.compile("^[a-zA-Z]+$");
		    Matcher matcher = alphaNum.matcher(input);
		    value = matcher.find();
		}
		
		if(value == false) {
			return false;
			//exit();
		}
		
		return true;
	}

  private static void exit() {
		System.out.println("Invalid");
		System.exit(255);
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
				byte[] shaInBytes = digest(key2.getBytes(UTF_8), algorithm);
				String result = new String(Base64.getEncoder().encode(shaInBytes));
				
				Serializable bookResult = unsealObject(fromStringToAESkey(result), fileName);
				Gradebook book = (Gradebook) bookResult;
				
				
				if(book == null || !(book.validateKey(key))){

					exit();
				}    

				return book;
			}
		} catch (Exception e){

			exit();
		}

		return null;
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
			byte[] shaInBytes = digest(key2.getBytes(UTF_8), algorithm);
			String result = new String(Base64.getEncoder().encode(shaInBytes));
			sealObject(book, fromStringToAESkey(result), fileName);
			
			}catch(Exception e) {

				exit();
			}
			
		} catch (Exception e) {
		

			exit();
		}
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
}
