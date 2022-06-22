import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.Serializable;

//import ...

/**
 * A helper class for your gradebook Some of these methods may be useful for
 * your program You can remove methods you do not need If you do not wiish to
 * use a Gradebook object, don't
 */
public class Gradebook implements Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private String name;
	private Map<String, Student> students;
	private Map<String, Assignment> assignments;

	/* Create a new gradebook */
	public Gradebook(String name, SecretKey key2) {
		this.name = name;
		key = convertStringToHex(Base64.getEncoder().encodeToString(key2.getEncoded()));
		students = new HashMap<String, Student>();
		assignments = new HashMap<String, Assignment>();
		System.out.println("Key is: " + key);
	}

	public String getName(String key) {
		if (!validateKey(key)) {
			exit();
		}
		return name;
	}
	

	/* Adds a student to the gradebook */
	public boolean addStudent(String key, String firstName, String lastName) {
		if (!validateKey(key)) {
			exit();
		}
		String name = lastName + ", " + firstName;
		Student currStudent = students.get(name);
		if (currStudent != null) {
			return false; 
		}

		students.put(name, new Student(firstName, lastName));
		return true;
	}

	/* Adds an assinment to the gradebook */
	public boolean addAssignment(String key, String name, Assignment assignment) {
		if (!validateKey(key)) {
		  exit();
		}

		if (assignments.containsKey(name) == true) {
			return false;
		}

		double weightCounter = 0;
		for (Assignment currAssign : assignments.values()) {
			weightCounter += currAssign.getWeight(this, key);
		}
		weightCounter += assignment.getWeight(this, key);

		if (weightCounter > 1) {
			return false;
		}

		assignments.put(name, assignment);
		return true;

	}

	public boolean deleteStudent(String key, String firstName, String lastName) {
		if (!validateKey(key)) {
			exit();
		}

		String name = lastName + ", " + firstName;
		if (students.containsKey(name) == false) {
			return false;
		}

		students.remove(name);
		return true;

	}

	public boolean deleteAssignment(String key, String name) {
		if (!validateKey(key)) {
			exit();
		}

		if (assignments.containsKey(name) == false) {
			return false;
		}

		for (Student student : students.values()) {
			student.deleteAssignment(this, key, name);
		}

		assignments.remove(name);
		return true;
	}

	/* Adds a grade to the gradebook */
	public boolean addGrade(String key, String firstName, String lastName, String assignmentName, int grade) {
		if (!validateKey(key)) {
			exit();
		}
		String name = lastName + ", " + firstName;

		if (students.containsKey(name) && assignments.containsKey(assignmentName)) {

			Student currStudent = students.get(name);
			currStudent.addGrade(this, key, assignmentName, assignments.get(assignmentName), grade);
		} else {
			return false;
		}

		return true;

	}

	public Map<String, Student> getStudents(String key) {
		if (!validateKey(key)) {
			exit();
		}

		return students;
	}

	public Map<String, Assignment> getAssignments(String key) {
		if (!validateKey(key)) {
			exit();
		}

		return assignments;
	}

	public boolean validateKey(String key) {
		if (this.key.compareTo(key) == 0) {
			return true;
		} else {
			return false;
		}
	}

	private static String convertStringToHex(String str) {
    
		char ch[] = str.toCharArray();
		StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < ch.length; i++) {
        	sb.append(Integer.toHexString((int) ch[i]));
    	}	
    	return sb.toString();
	}

	private static void exit() {
		System.out.println("Invalid");
		System.exit(255);
	}
}
