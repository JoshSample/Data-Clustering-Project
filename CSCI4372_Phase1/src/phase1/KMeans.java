package phase1;

//Joshua Sample
//CSCI 4372
//Phase 1: Basic K-Means

// imports used
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

// this class holds all the methods needed for the algorithm
public class KMeans {
	private String f;	// File name
	private int k;		// Number of clusters
	private int i;		// Max iterations
	private double t;	// Conversion threshold
	private int r;		// Number of runs
	private Points[] points; 	// Holds the points read from file
	private Points[][] clusteredPoints; 	// Holds clusters, one thing to note is that the first element for each cluster is the centroid
	private int numOfPoints;	// Number of points, obtained from file
	private int dimensionality;	// Dimensionality of points, obtained from file
	private double sse;	// tracks SSE value
	private int bestRun;	// tracks best run (lowest SSE value)
	private double bestSSE;	// tracks best SSE (lowest SSE value)
	
	// default constructor, sets values from command line
	public KMeans(String a, int b, int c, double d, int e) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
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
			clusteredPoints = new Points[k][numOfPoints+1];
			points = new Points[numOfPoints];
			int a = 0;
			while ((line = lineReader.readLine())!=null) {
				String[] token2 = line.split(" ");
				Points temp = new Points();
				for(int i = 0; i < token2.length; i++) {
					temp.addAttributes(Double.parseDouble(token2[i]));
				}
				points[a] = temp;
				a++;
			}
		} catch (Exception e) {
			System.err.println("There was a problem with the file reader.");
		}
	}
	
	// Since we only need to compare distance between a point and a centroid,
	// this isn't a perfect implementation of euclidean distance as it would
	// typically have a sqrt function. This however keeps computation time down.
	// This method is also used to calculate the SSE
	public double euclideanDistance(Points a, Points b) { 
		double total = 0;
		double diff;
		for (int i = 0; i < dimensionality; i++) {
			diff = b.getAttributes().get(i) - a.getAttributes().get(i);
			total += diff * diff;
		}
		return total;
	}
	
	// randomly generates initial centroids
	public void initCentroids() {
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			int x = rand.nextInt(numOfPoints);	// int x will be a random index for points[], which will be the initial centroid
			clusteredPoints[i][0] = points[x];
		}
	}
	
	// recalculate centroids based on mean
	public void calculateCentroids() {
		// this nested loop iterates over clusteredPoints
		for (int a = 0; a < k; a++) {
			double[] attributes = new double[dimensionality];	// This will eventually have the mean attributes for each attribute
			double size = 0;	// needed to calculate mean attributes
			for (int b = 1; b <= numOfPoints; b++) {	// int b starts at 1 since the first element of clusteredPoints is the centroid
				if (clusteredPoints[a][b] != null) {
					size++;
					for (int c = 0; c < dimensionality; c++) {
						attributes[c] += clusteredPoints[a][b].getAttributes().get(c);	// add each attribute for every point in a cluster
					}
				}
			}
			Points point = new Points();	// this will become the new centroid
			for (int b = 0; b < dimensionality; b++) {
				attributes[b] = attributes[b]/size;	// average out the attributes
				point.addAttributes(attributes[b]);	// add averaged attributes for the new centroid
			}
			clusteredPoints[a][0] = point;	// replace old centroid with new one
		}
		// once new centroid has been calculated, this loop resets the points
		for (int a = 0; a < k; a++) {
			for (int b = 1; b <= numOfPoints; b++) {
				clusteredPoints[a][b] = null;
			}
		}
	}
	
	// classifies points based on euclidean distance
	public int classifyPoint(int j) {
		double distance = 0;
		double min = Double.MAX_VALUE;
		int index = 0;	// this is returned, and is the index of the closest cluster to a given point
		for (int i = 0; i < k; i++) {
			distance = euclideanDistance(clusteredPoints[i][0], points[j]);
			if (distance < min) {
				min = distance;
				index = i;
			}
		}
		return index;
	}
	
	// calculates sum of squared error value
	public double sumOfSquaredErrors() {
		sse = 0;	// this resets the sse value if sse was previously calculated
		for (int a = 0; a < k; a++) {
			for (int b = 1; b <= numOfPoints; b++) {
				if (clusteredPoints[a][b] != null) {
					sse += euclideanDistance(clusteredPoints[a][0], clusteredPoints[a][b]);
				}
			}
		}
		return sse;
	}
	
	// the k-means algorithm
	public void kMeans() {
		readFile();	// read file to get points
		bestRun = Integer.MAX_VALUE;	// this sets bestRun
		bestSSE = Double.MAX_VALUE;	// this sets bestSSE
		int counter;	// counter is needed for max iterations
		boolean improvement;	// improvement is based off SSE value, exits while loop when false
		double sse1;	// variable for previous SSE value
		double sse2;	// variable for current SSE value
		
		// code wrapped in try/catch due to file writing
		try {
			FileWriter fw = new FileWriter("results_" + f);	// results stored in file "results.txt"
			BufferedWriter myOutfile = new BufferedWriter(fw);
			myOutfile.write("test " + f + " " + k + " " + i + " " + t + " " + r + "\n");
			
			// this loop is for the number of runs
			for (int a = 0; a < r; a++) {
				myOutfile.write("\nRun " + (a + 1) + "\n" + "-----\n");
				initCentroids();	// get initial centroids
				counter = 1;
				improvement = true;
				
				// this loop is for the iterations, and can exit if improvement is false (based on SSE improvement)
				while (counter <= this.i && improvement == true) {
					
					// classifies each point to an appropriate cluster
					for (int j = 0; j < numOfPoints; j++) {
						int index = classifyPoint(j);
						clusteredPoints[index][j+1] = points[j];
					}
					
					sse1 = sse;	// sse1 equals previous sse value
					sse2 = sumOfSquaredErrors();	// sse2 equals current value
					
					myOutfile.write("Iteration " + counter + ": SSE = " + sse2 + "\n");
					
					// these if statements check if sse value has improved
					if (counter > 1) {
						if ((sse1 - sse2) / sse1 < t) {
							improvement = false;
							if (sse2 < bestSSE) {
								bestSSE = sse2;
								bestRun = a + 1;
							}
						}
					}
					calculateCentroids();	// calculate centroids based on mean
					counter++;	// increment counter
				}
			}
			
			myOutfile.write("\nBest Run: " + bestRun + ": SSE = " + bestSSE);
			
			myOutfile.flush();
			myOutfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not save results.");
		}
	}

	
}
