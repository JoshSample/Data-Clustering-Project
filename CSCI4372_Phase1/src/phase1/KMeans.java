package phase1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class KMeans {
	private String f;	// File name
	private int k;		// Number of clusters
	private int i;		// Max iterations
	private double t;	// Conversion threshold
	private int r;		// Number of runs
	private ArrayList<Points> points = new ArrayList<Points>();
	private HashMap<Points, ArrayList<Points>> clusteredPoints = new HashMap<Points, ArrayList<Points>>();
	private int numOfPoints;
	private int dimensionality;
	private int[] centroids;
	
	public KMeans(String a, int b, int c, double d, int e) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
		centroids = new int[k];
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
	
	// reads file, parsed data is as follows:
	// after the first line, each line is a point and thus stored in ArrayList<Points>
	// each attribute in the lines, separated by blanks, are new attributes for a given point
	public void readFile() {
		BufferedReader lineReader = null;
		try {
			FileReader fr = new FileReader("./" + f);
			lineReader = new BufferedReader(fr);
			String line = null;
			line = lineReader.readLine();
			String[] token = line.split(" ");
			numOfPoints = Integer.parseInt(token[0]);
			dimensionality = Integer.parseInt(token[1]);
			while ((line = lineReader.readLine())!=null) {
				String[] token2 = line.split(" ");
				Points temp = new Points();
				for(int i = 0; i < token2.length; i++) {
					temp.addAttributes(Double.parseDouble(token2[i]));
				}
				points.add(temp);
			}
		} catch (Exception e) {
			System.err.println("There was a problem with the file reader.");
		}
	}
	
	// Since we only need to compare distance between a point and a centroid,
	// this isn't a perfect implementation of euclidean distance as it would
	// typically have a sqrt function. This however keeps computation time down.
	public double euclideanDistance(Points a, Points b) { 
		double total = 0;
		double diff;
		for (int i = 0; i < a.getAttributes().size(); i++) {
			diff = b.getAttributes().get(i) - a.getAttributes().get(i);
			total += diff * diff;
		}
		return total;
	}
	
	// randomly generates initial centroids
	public void initCentroids() {
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			centroids[i] = rand.nextInt(numOfPoints);
			clusteredPoints.put(points.get(centroids[i]), points.get(centroids[i]));
		}
	}
	
	public int calculateCentroids() {
		return 0;
	}
	
	public int classifyPoint(int j) {
		double distance = 0;
		double min = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < k; i++) {
			distance = euclideanDistance(points.get(j), points.get(centroids[i]));
			if (distance < min) {
				min = distance;
				index = i;
			}
		}
		return index;
	}
	
	public double sumOfSquaredErrors() {
		double sse = 0;
		double centroid = 0;
		for (int a = 0; a < k; a++) {
			centroid = clusteredPoints.get(points.get(centroids[a])).sumAttributes();
		}
		
		return sse;
	}
	
	public void kMeans() {
		readFile();
		int counter = 0;
		boolean improvement = true;
		for (int a = 0; a < r; a++) {
			initCentroids();
			while (counter < this.i || improvement == false) {
				for (int j = 0; j < numOfPoints; j++) {
					int index = classifyPoint(j);
					clusteredPoints.put(points.get(centroids[index]), points.get(j));
				}
				sumOfSquaredErrors();
				counter++;
			}
		}
	}
	
	
}
