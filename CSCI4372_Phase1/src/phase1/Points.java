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
	
	public String toString() {
		return attribute.toString();
	}
}
