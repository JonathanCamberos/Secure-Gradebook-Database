import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

//import ...

/**
 * Prints out a gradebook in a few ways Some skeleton functions are included
 */
public class gradebookdisplay {
	static boolean verbose = false;

	private static void print_Assignment(Gradebook book, String key, String assignmentName, boolean A, boolean G) {

		Map<String, Student> students = book.getStudents(key);
		Map<String, Assignment> assignments = book.getAssignments(key);

		if (!assignments.containsKey(assignmentName)) {
			exit();
		}

		ArrayList<String> names = new ArrayList<>(students.keySet());

		if (A) {
			Collections.sort(names);

			for (String name : names) {
				System.out.println(
						"(" + name + ", " + (int) students.get(name).getGrade(book, key, assignmentName, null) + ")");
			}
		} else if (G) {
			ArrayList<String> list = new ArrayList<>();
			for (String name : names) {
				list.add((int) students.get(name).getGrade(book, key, assignmentName, null) + "$(" + name + ", "
						+ (int) students.get(name).getGrade(book, key, assignmentName, null) + ")");
			}

			Collections.sort(list);
			Collections.reverse(list);

			for (String s : list) {
				System.out.println(s.substring(s.indexOf("$") + 1));
			}
		}

		return;
	}

	private static void print_Student(Gradebook book, String key, String first, String last) {

		Map<String, Student> students = book.getStudents(key);

		if (!students.containsKey(last + ", " + first)) {
			exit();
		}

		Student student = students.get(last + ", " + first);
		Map<String, Double> assignments = student.getGrades(book, key);

		for (Map.Entry<String, Double> entry : assignments.entrySet()) {
			System.out.println("(" + entry.getKey() + ", " + entry.getValue().intValue() + ")");
		}

		return;
	}

	private static void print_Final(Gradebook book, String key, boolean A, boolean G) {

		Map<String, Student> students = book.getStudents(key);
		Map<String, Assignment> assignments = book.getAssignments(key);
		Map<String, String> finalGrades = new HashMap<String, String>();
		Map<String, Double> finalNames = new HashMap<String, Double>();

		ArrayList<String> names = new ArrayList<>(students.keySet());

		for (String student : students.keySet()) {
			Map<String, Double> grades = students.get(student).getGrades(book, key);
			double total = 0.0;
			for (String assignment : grades.keySet()) {
				total += (grades.get(assignment) / assignments.get(assignment).getPoints(book, key))
						* assignments.get(assignment).getWeight(book, key);
			}
			finalGrades.put(total + "(" + student + ", " + total + ")", "(" + student + ", " + total + ")");
			finalNames.put("(" + student + ", " + total + ")", total);
		}

		if (A) {
			ArrayList<String> list = new ArrayList<String>(finalNames.keySet());
			Collections.sort(list);

			for (String name : list) {
				System.out.println(name);
			}
		} else if (G) {

			ArrayList<String> list = new ArrayList<>(finalGrades.keySet());
			Collections.sort(list);
			Collections.reverse(list);

			for (String i : list) {
				System.out.println(finalGrades.get(i));
			}
		}
		return;
	}

	private static void exit() {
		System.out.println("Invalid");
		System.exit(255);
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("\nNo Extra Command Line Argument Passed Other Than Program Name");
		} else if (args.length >= 5) {
			String gradebook = "", key = "", action = "";
			if (!args[0].equals("-N") || !args[2].equals("-K")) {
				exit();
			} else if (!args[4].equals("-PA") && !args[4].equals("-PS") && !args[4].equals("-PF")) {
				exit();
			}

			validate(args[1], 1);
			gradebook = args[1];
			key = args[3];
			action = args[4];

			if (getGradebook(gradebook, key) == null) {
				exit();
			}

			String assignmentName = null, firstName = null, lastName = null;
			boolean A = false, G = false;
			int counter = 5;

			if (action.equals("-PA")) {
				while (counter < args.length) {
					if (args[counter].equals("-AN")) {
						if (counter + 1 < args.length) {
							validate(args[counter + 1], 2);
							assignmentName = args[counter + 1];
							counter++;
						} else {
							exit();
						}
					} else if (args[counter].equals("-A")) {
						A = true;
					} else if (args[counter].equals("-G")) {
						G = true;
					} else {
						exit();
					}

					counter++;
				}
				// print_Assignment(setup.getGradebook(gradebook), key, assignmentName, A, G);
				if (A == true && G == true) {
						exit();
					} else if (A == false && G == false) {
						exit();
				}
				print_Assignment(getGradebook(gradebook, key), key, assignmentName, A, G);

			} else if (action.equals("-PS")) {
				while (counter < args.length) {
					if (args[counter].equals("-FN")) {
						if (counter + 1 < args.length) {
							validate(args[counter + 1], 3);
							firstName = args[counter + 1];
							counter++;
						} else {
							exit();
						}
					} else if (args[counter].equals("-LN")) {
						if (counter + 1 < args.length) {
							validate(args[counter + 1], 3);
							lastName = args[counter + 1];
							counter++;
						} else {
							exit();
						}
					} else {
						exit();
					}
					counter++;
				}
				// print_Student(setup.getGradebook(gradebook), key, firstName, lastName);
				print_Student(getGradebook(gradebook, key), key, firstName, lastName);

			} else {
				while (counter < args.length) {
					if (args[counter].equals("-A")) {
						A = true;
					} else if (args[counter].equals("-G")) {
						G = true;
					} else {
						exit();
					}

					if (A && G) {
						exit();
					}
					counter++;
				}
				// print_Final(setup.getGradebook(gradebook), key, A,G);
				if (A == true && G == true) {
						exit();
					} else if (A == false && G == false) {
						exit();
				}
				print_Final(getGradebook(gradebook, key), key, A, G);

			}
		} else {
			exit();
		}
	}

	private static boolean validate(String input, int type) {
		boolean value = false;
		if (type == 1) {
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

		if (value == false) {
			return false;
			// exit();
		}

		return true;
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



    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
	
	  private static SecretKey fromStringToAESkey(String s) {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(s);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		return originalKey;
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
		} catch (Exception e) {
		

			exit();
		}

		return null;
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
