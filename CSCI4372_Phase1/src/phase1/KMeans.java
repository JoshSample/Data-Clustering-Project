package phase1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class KMeans {
	private String f;	// File name
	private int k;		// Number of clusters
	private int i;		// Max iterations
	private double t;	// Conversion threshold
	private int r;		// Number of runs
	private ArrayList<Double> attributes; // Holds all values from txt file
	
	public KMeans(String a, int b, int c, double d, int e) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
	}
	
	public String getFName() {
		return f;
	}
	
	public int getClusters() {
		return k;
	}
	
	public int getIters() {
		return i;
	}
	
	public double getThreshold() {
		return t;
	}
	
	public int getRuns() {
		return r;
	}
	
	public ArrayList<Double> getPoints() {
		return attributes;
	}
	
	public void readFile() {
		BufferedReader lineReader = null;
		try {
			FileReader fr = new FileReader("./" + f);
			lineReader = new BufferedReader(fr);
			String line = null;
			while ((line = lineReader.readLine())!=null) {
				String[] token = line.split(" ");
				String numOfPoints = token[0];
				String dimensionality = token[1];
				for( int i = 2; i < token.length; i++) {
					attributes.add(Double.parseDouble(token[i]));
				}	
			}
		} catch (Exception e) {
			System.err.println("there was a problem with the file reader, try different read type.");
		}
	}
	
	
}
