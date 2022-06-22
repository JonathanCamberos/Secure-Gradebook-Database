import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;

public class Student implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String firstName, lastName;
	private Map<String, Double> grades;
	
	public Student(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		grades = new TreeMap<String, Double>();
	}
	
	public boolean addGrade(Gradebook book, String key, String assignmentName, Assignment assignment, double grade) {
		if(!book.validateKey(key)) {
			  exit();
		  }

		grades.put(assignmentName, grade);
		return true;
	}
	
	public double getGrade(Gradebook book, String key, String assignmentName, Assignment assignment) {
		if(!book.validateKey(key)) {
			  exit();
		  }
		if(!grades.containsKey(assignmentName)) {
			return 0.0;
		}

		return grades.get(assignmentName);
	}
	
	public boolean deleteAssignment(Gradebook book, String key, String assignmentName) {
		if(!book.validateKey(key)) {
			  exit();
		  }
		
		grades.remove(assignmentName);
		return true;
	}
	
	public Map<String, Double> getGrades(Gradebook book, String key) {
		  if(!book.validateKey(key)) {
			  exit();
		  }
		  
		  return grades;

	  }
	
	public boolean studentExists(Gradebook book, String key, String firstName, String lastName) {
		if(!book.validateKey(key)) {
			  exit();
		  }
		
		if(this.firstName.equals(firstName) && this.lastName.equals(lastName)) {
			return true;
		} else {
			return false;
		}
	}
	
	 private static void exit() {
			System.out.println("Invalid");
			System.exit(255);
	}
}
