package phase1;

//Joshua Sample
//CSCI 4372
//Phase 1: Basic K-Means

import java.util.ArrayList;

// Points class is a single point, read from the input file,
// that holds n attributes, also read from file
public class Points {
	private ArrayList<Double> attribute;	// attribute is an ArrayList that stores doubles, gotten from the input file
	
	// default constructor, instantiates ArrayList
	public Points() {
		attribute = new ArrayList<Double>();
	}
	
	// adds attributes to the Points object
	public void addAttributes(double a) {
		attribute.add(a);
	}
	
	// returns attributes from the ArrayList
	public ArrayList<Double> getAttributes() {
		return attribute;
	}
	
	// sums the attributes in the point
	public double sumAttributes() {
		double sum = 0;
		for (int i = 0; i < attribute.size(); i++) {
			sum += attribute.get(i);
		}
		return sum;
	}
	
	// returns a string representation of the ArrayList
	public String toString() {
		return attribute.toString();
	}
}
