package phase1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class KMeans {
	private String f;	// File name
	private int k;		// Number of clusters
	private int i;		// Max iterations
	private double t;	// Conversion threshold
	private int r;		// Number of runs
	private ArrayList<Points> points;
	private int numOfPoints;
	private int dimensionality;
	
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
	
	public ArrayList<Points> getPoints() {
		return points;
	}
	
	public void readFile() {
		BufferedReader lineReader = null;
		try {
			FileReader fr = new FileReader("./" + f);
			lineReader = new BufferedReader(fr);
			String line = null;
			while ((line = lineReader.readLine())!=null) {
				String[] token = line.split(" ");
				String[] token2 = line.split("\n");
				numOfPoints = Integer.parseInt(token[0]);
				dimensionality = Integer.parseInt(token[1]);
				for(int i = 1; i < token2.length; i++) {
					Points temp = new Points();
					for(int j = 0; j < token.length; j++)
						temp.addAttributes(Double.parseDouble(token[j]));
					points.add(temp);
				}
			}
		} catch (Exception e) {
			System.err.println("There was a problem with the file reader.");
		}
	}
	
	
	
	public void kMeans() {
		readFile();
		int centroids[] = new int[k];
		for (int i = 0; i < k; i++) {
			Random rand = new Random();
			centroids[i] = rand.nextInt(numOfPoints);
		}
	}
	
	
}
