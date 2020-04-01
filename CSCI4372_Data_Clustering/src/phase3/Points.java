package phase3;

//Joshua Sample
//CSCI 4372
//Phase 3: Internal Validation

import java.util.ArrayList;

// Points class is a single point, read from the input file,
// that holds n attributes, also read from file
public class Points {
	private ArrayList<Double> attribute;	// attribute is an ArrayList that stores doubles, gotten from the input file
	private double maxAttribute;			// this is the max attribute for a given point
	private double minAttribute;			// this is the minimum attribute for a given point
	
	// default constructor, instantiates ArrayList
	public Points() {
		attribute = new ArrayList<Double>();
		maxAttribute = 0;
		minAttribute = 0;
	}
	
	// adds attributes to the Points object
	public void addAttributes(double a) {
		attribute.add(a);
	}
	
	// returns attributes from the ArrayList
	public ArrayList<Double> getAttributes() {
		return attribute;
	}
	
	// this sets the minimum attribute for a point
	public void setMinAttribute(double min) {
		minAttribute = min;
	}
	
	// this sets the maximum attribute for a point
	public void setMaxAttribute(double max) {
		maxAttribute = max;
	}
	
	// this performs min-max normalization for an attribute of a given point in the range [0, 1]
	public double minMaxNormalization(double a) {
		double newAttribute = (a - minAttribute) / (maxAttribute - minAttribute) * (1 - 0) + 0;
		return newAttribute;
	}
	
	// returns a string representation of the ArrayList
	public String toString() {
		return attribute.toString();
	}
}
