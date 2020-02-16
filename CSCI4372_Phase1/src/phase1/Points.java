package phase1;

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
