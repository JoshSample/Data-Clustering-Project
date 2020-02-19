package phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class KMeans {
	private String f;	// File name
	private int k;		// Number of clusters
	private int i;		// Max iterations
	private double t;	// Conversion threshold
	private int r;		// Number of runs
	private ArrayList<Points> points = new ArrayList<Points>();
	private Points[][] clusteredPoints;
	private int numOfPoints;
	private int dimensionality;
	private double[] centroids;
	private double sse;
	
	public KMeans(String a, int b, int c, double d, int e) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
		centroids = new double[k];
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
			clusteredPoints = new Points[k][numOfPoints];
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
			if (b.getAttributes().size() > 1) {
				diff = b.getAttributes().get(i) - a.getAttributes().get(i);
				total += diff * diff;
			}
			else {
				diff = b.getAttributes().get(0) - a.getAttributes().get(i);
				total += diff * diff;
			}
		}
		return total;
	}
	
	// randomly generates initial centroids
	public void initCentroids() {
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			centroids[i] = rand.nextInt(numOfPoints);
			clusteredPoints[i][0] = points.get((int)centroids[i]);
		}
	}
	
	public void calculateCentroids() {
		double size = 0;
		double sum = 0;
		for (int a = 0; a < k; a++) {
			for (int b = 0; b < numOfPoints; b++) {
				if (clusteredPoints[a][b] != null) {
					double point = points.get(b).sumAttributes();
					sum += point;
					size++;
				}
			}
			centroids[a] = sum / size;
			Points point = new Points();
			point.addAttributes(centroids[a]);
			clusteredPoints[a][0] = point;
		}
	}
	
	// classifies points based on euclidean distance
	public int classifyPoint(int j) {
		double distance = 0;
		double min = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < k; i++) {
			distance = euclideanDistance(points.get(j), clusteredPoints[i][0]);
			if (distance < min) {
				min = distance;
				index = i;
			}
		}
		return index;
	}
	
	public double sumOfSquaredErrors() {
		sse = 0;
		double centroid;
		for (int a = 0; a < k; a++) {
			centroid = clusteredPoints[a][0].sumAttributes();
			for (int b = 0; b < numOfPoints; b++) {
				if (clusteredPoints[a][b] != null) {
					double point = points.get(b).sumAttributes();
					double diff = point - centroid;
					diff *= diff;
					sse += diff;
				}
			}
		}
		return sse;
	}
	
	public void kMeans() {
		readFile();
		try {
			FileWriter fw = new FileWriter("results.txt");
			BufferedWriter myOutfile = new BufferedWriter(fw);
			for (int a = 0; a < r; a++) {
				myOutfile.write("\nRun " + (a + 1) + "\n" + "-----\n");
				initCentroids();
				int counter = 1;
				boolean improvement = true;
				while (counter <= this.i && improvement == true) {
					for (int j = 0; j < numOfPoints; j++) {
						int index = classifyPoint(j);
						clusteredPoints[index][j] = points.get(index);
					}
					double sse1 = sse;
					double sse2 = sumOfSquaredErrors();
					myOutfile.write("Iteration " + counter + ": SSE = " + sse2 + "\n");
					if (counter > 1) {
						if ((sse1 - sse2) / sse1 < t)
							improvement = false;
					}
					calculateCentroids();
					counter++;
				}
				
			}
			myOutfile.flush();
			myOutfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not save results.");
		}
	}

	
}
