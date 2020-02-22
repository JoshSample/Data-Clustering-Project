package phase1;

//Joshua Sample
//CSCI 4372
//Phase 1: Basic K-Means

import java.util.ArrayList;

public class Points {
	private ArrayList<Double> attribute;
	
	public Points() {
		attribute = new ArrayList<Double>();
	}
	
	public void addAttributes(double a) {
		attribute.add(a);
	}
	
	public ArrayList<Double> getAttributes() {
		return attribute;
	}
	
	public double sumAttributes() {
		double sum = 0;
		for (int i = 0; i < attribute.size(); i++) {
			sum += attribute.get(i);
		}
		return sum;
	}
	
	public String toString() {
		return attribute.toString();
	}
}
