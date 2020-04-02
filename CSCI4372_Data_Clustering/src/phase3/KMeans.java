package phase3;

//Joshua Sample
//CSCI 4372
//Phase 3: Internal Validation

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
	private Points[] centroids;	// Holds centroids
	private Points[][] clusteredPoints; 	// Holds clustered points
	private int numOfPoints;	// Number of points, obtained from file
	private int dimensionality;	// Dimensionality of points, obtained from file
	private double sse;	// tracks SSE value
	private double bestFinalSSE;	// tracks best final SSE (lowest SSE value for every run)
	private double minAttribute;	// this is the minimum attribute for a given point
	private double maxAttribute;	// this is the max attribute for a given point
	private int kMax;	// max number of clusters
	private int kMin = 2;	// minimum number of clusters
	private double S_b;	// trace of the between cluster scatter
	
	// default constructor, sets values from command line
	public KMeans(String a, int b, int c, double d, int e, Points[] p, int num, int dim, int km) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
		points = p;
		numOfPoints = num;
		dimensionality = dim;
		clusteredPoints = new Points[k][numOfPoints];
		centroids = new Points[k];
		kMax = km;
	}
	
	// reads file, parsed data is as follows:
	// after the first line, each line is a point and thus stored in the array points[].
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
			centroids = new Points[k];
			points = new Points[numOfPoints];
			kMax = (int) Math.round(Math.sqrt(numOfPoints/2));
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
	
	// this method finds the minimum and maximum attributes for all points
	public void findMinMax() {
		maxAttribute = points[0].getAttributes().get(0);
		minAttribute = points[0].getAttributes().get(0);
		for (int i = 0; i < numOfPoints; i++) {
			for (int j = 0; j < dimensionality; j++) {
				if (points[i].getAttributes().get(j) < minAttribute)
					minAttribute = points[i].getAttributes().get(j);
				if (points[i].getAttributes().get(j) > maxAttribute)
					maxAttribute = points[i].getAttributes().get(j);
			}
		}
		// set each points min and max attributes to the ones found in the previous loop
		for (int i = 0; i < numOfPoints; i++) {
			points[i].setMaxAttribute(maxAttribute);
			points[i].setMinAttribute(minAttribute);
		}
	}
	
	// this method normalizes attributes for every point using min-max normalization
	public void normalizeAttributes() {
		for (int i = 0; i < numOfPoints; i++) {
			for (int j = 0; j < dimensionality; j++) {
				points[i].getAttributes().set(j, points[i].minMaxNormalization(points[i].getAttributes().get(j)));
			}
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
	
	// this creates random clusters, for random partition method of initialization
	public void randClusters() {
		Random rand = new Random();
		for (int i = 0; i < numOfPoints; i++) {
			int x = rand.nextInt(k);
			clusteredPoints[x][i] = points[i];
		}
	}
	
	// calculate centroids based on mean
	public void calculateCentroids() {
		// this nested loop iterates over clusteredPoints
		for (int a = 0; a < k; a++) {
			double[] attributes = new double[dimensionality];	// This will eventually have the mean attributes for each attribute
			double size = 0;	// needed to calculate mean attributes
			for (int b = 0; b < numOfPoints; b++) {	
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
			centroids[a] = point;	// replace old centroid with new one
		}
		// once new centroid has been calculated, this loop resets the points
		for (int a = 0; a < k; a++) {
			for (int b = 0; b < numOfPoints; b++) {
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
			distance = euclideanDistance(centroids[i], points[j]);
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
			for (int b = 0; b < numOfPoints; b++) {
				if (clusteredPoints[a][b] != null) {
					sse += euclideanDistance(centroids[a], clusteredPoints[a][b]);
				}
			}
		}
		return sse;
	}
	
	public void findBetweenClusterScatter() {
		double tSSE = 0;	// total sum of squares value
		double[] attributes = new double[dimensionality];	// This will have the mean attributes for each attribute in the data set
		// first thing is to find mean point for the data set
		for (int i = 0; i < numOfPoints; i++) {	
			for (int j = 0; j < dimensionality; j++) {
				attributes[j] += points[i].getAttributes().get(j);	// add each attribute for every point in the data set
			}
		}
		Points meanPoint = new Points();	// the meanPoint is the average point in the data set
		for (int i = 0; i < dimensionality; i++) {
			attributes[i] = attributes[i]/numOfPoints;	// average out the attributes
			meanPoint.addAttributes(attributes[i]);	// add averaged attributes for the mean
		}
		// once mean point has been calculated, perform sum of squares on each point to the mean
		for (int i = 0; i < numOfPoints; i++) {
			tSSE += euclideanDistance(meanPoint, points[i]);
		}
		// once total sum of squares has been calculated, subtract tSSE from the SSE value
		S_b = tSSE - bestFinalSSE;
	}
	
	// the k-means algorithm
	public void kMeans() {
		int counter;	// counter is needed for max iterations
		boolean improvement;	// improvement is based off SSE value, exits while loop when false
		double sse1;	// variable for previous SSE value
		double sse2;	// variable for current SSE value
		
		// code wrapped in try/catch due to file writing
		try {
			FileWriter fw = new FileWriter("results_" + f + "_" + k + ".txt");	// results stored in file "results.txt"
			BufferedWriter myOutfile = new BufferedWriter(fw);
			myOutfile.write("test " + f + " " + k + " " + i + " " + t + " " + r + "\n");
			//findMinMax();			// finds min/max attributes before normalization
			//normalizeAttributes();	// normalizes attributes for each point

			double CH = 0;	// the Calinski–Harabasz value
			bestFinalSSE = Double.MAX_VALUE;	// sets bestFinalSSE
			// this loop is for the number of runs
			for (int a = 0; a < r; a++) {
				myOutfile.write("\nRun " + (a + 1) + "\n" + "-----\n");
				randClusters();		// perform random partition
				calculateCentroids();	// get centroids from partition
				counter = 1;
				improvement = true;

				// this loop is for the iterations, and can exit if improvement is false (based on SSE improvement)
				while (counter <= this.i && improvement == true) {

					// classifies each point to an appropriate cluster
					for (int j = 0; j < numOfPoints; j++) {
						int index = classifyPoint(j);
						clusteredPoints[index][j] = points[j];
					}

					sse1 = sse;	// sse1 equals previous sse value
					sse2 = sumOfSquaredErrors();	// sse2 equals current value

					myOutfile.write("Iteration " + counter + ": SSE = " + sse2 + "\n");

					// these if statements check if sse value has improved
					if (counter > 1) {
						if ((sse1 - sse2) / sse1 < t) {
							improvement = false;
							if (sse2 < bestFinalSSE) {
								bestFinalSSE = sse2;
							}
						}
					}
					calculateCentroids();	// calculate centroids based on mean
					counter++;	// increment counter
				}
			}
			findBetweenClusterScatter();
			CH = ((numOfPoints - k) / (k - 1)) * (S_b / bestFinalSSE);
			myOutfile.write("\nCalinski–Harabasz Score: " + CH);



			myOutfile.flush();
			myOutfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not save results.");
		}
	}

	
}
