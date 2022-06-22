import java.io.Serializable;
public class Assignment implements Serializable{

    private static final long serialVersionUID = 1L;
    private final String name;
    private double points;
    private double weight;

    public Assignment(String name, double points, double weight){
        this.name = name;
        this.points = points;
        this.weight = weight;
    }


    public String getName(Gradebook book, String key){
    	if(!book.validateKey(key)) {
			  exit();
		 }
        return name;
    }

    public double getPoints(Gradebook book, String key){
    	if(!book.validateKey(key)) {
			  exit();
		 }
        return points;
    }

    public double getWeight(Gradebook book, String key){
    	if(!book.validateKey(key)) {
			  exit();
		 }
        return weight;
    }

    private static void exit() {
		System.out.println("Invalid");
		System.exit(255);
    }
}
