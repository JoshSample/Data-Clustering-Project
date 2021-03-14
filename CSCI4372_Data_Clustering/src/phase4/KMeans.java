package phase4;

//Joshua Sample
//CSCI 4372
//Phase 4: External Validation

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
	private Points trueClusters[][];	// true clusters based on the data from the file
	private int numOfPoints;	// Number of points, obtained from file
	private int dimensionality;	// Dimensionality of points, obtained from file
	private double sse;	// tracks SSE value
	private double bestFinalSSE;	// tracks best final SSE (lowest SSE value for every run)
	private double minAttribute;	// this is the minimum attribute for a given point
	private double maxAttribute;	// this is the max attribute for a given point
	private int TP;		// true positive
	private int TN;		// true negative
	private int FP;		// false positive
	private int FN;		// false negative
	private double bestRand;	// tracks best Rand Statistic value
	private double bestJaccard;	// tracks best Jaccard Coefficient value
	private double bestFM;		// tracks best Fowlkes-Mallows Index value
	
	// default constructor, sets values from command line
	public KMeans(String a, int b, int c, double d, int e, Points[] p, int num, int dim, Points[][] tC) {
		f = a;
		k = b;
		i = c;
		t = d;
		r = e;
		points = p;
		numOfPoints = num;
		dimensionality = dim;
		trueClusters = new Points[k][numOfPoints];
		trueClusters = tC;
		clusteredPoints = new Points[k][numOfPoints];
		centroids = new Points[k];
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
	
	// randomly generates initial centroids
	public void initCentroids() {
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			int x = rand.nextInt(numOfPoints);	// int x will be a random index for points[], which will be the initial centroid
			centroids[i] = points[x];
		}
	}
	
	// recalculate centroids based on mean
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
	
	// finds TP, TN, FP, and FN values by comparing trueClusters to the clusters created by the algorithm
	public void findPairwiseMeasures() {
		// sets values to 0
		TP = 0; 
		TN = 0;
		FP = 0;
		FN = 0;
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < numOfPoints; j++) {
				if (trueClusters[i][j] != null || clusteredPoints[i][j] != null) {
					// if the point is in both clusters TP increases
					if (trueClusters[i][j] == clusteredPoints[i][j])
						TP++;
					// if the point is in the generated cluster but not in the trueCluster FP increases
					else if (trueClusters[i][j] == null && clusteredPoints[i][j] != null) 
						FP++;
					// if the point is in the trueCluster but not in the generated cluster FN increases
					else if (trueClusters[i][j] != null && clusteredPoints[i][j] == null) 
						FN++;
				}
				// else both clusters have the point not in the specific cluster so TN increases
				else
					TN++;
			}
		}
	}
	
	// Calculates Rand Statistic
	public double randStatistic() {
		double rand;
		double N = TP + TN + FP + FN;	// total of the pairwise measurements
		rand = (TP + TN) / N;
		return rand;
	}
	
	// Calculates the Jaccard Coefficient
	public double jaccardCoefficient() {
		double temp = TP + FP + FN;
		double jaccard = TP / temp;
		return jaccard;
	}
	
	// Calculates the Fowlkes-Mallows Index
	public double fowlkesMallows() {
		double temp1 = TP + FP;
		double temp2 = TP + FN;
		double prec = TP / temp1;
		double recall = TP / temp2;
		double fm = Math.sqrt(prec * recall);
		return fm;
	}
	
	// the k-means algorithm
	public void kMeans() {
		// sets tracking variables to values meant to be changed
		bestFinalSSE = Double.MAX_VALUE;
		bestRand = Double.MIN_VALUE;
		bestJaccard = Double.MIN_VALUE;
		bestFM = Double.MIN_VALUE;
		int counter;	// counter is needed for max iterations
		boolean improvement;	// improvement is based off SSE value, exits while loop when false
		double sse1;	// variable for previous SSE value
		double sse2;	// variable for current SSE value
		
		// code wrapped in try/catch due to file writing
		try {
			FileWriter fw = new FileWriter("results_" + f);	// results stored in file "results.txt"
			BufferedWriter myOutfile = new BufferedWriter(fw);
			myOutfile.write("test " + f + " " + k + " " + i + " " + t + " " + r + "\n");
			findMinMax();			// finds min/max attributes before normalization
			normalizeAttributes();	// normalizes attributes for each point
			
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
						clusteredPoints[index][j] = points[j];
					}
					
					sse1 = sse;	// sse1 equals previous sse value
					sse2 = sumOfSquaredErrors();	// sse2 equals current value
					
					myOutfile.write("Iteration " + counter + ": SSE = " + sse2 + "\n");
			
					// these if statements check if sse value has improved
					if (counter > 1) {
						if ((sse1 - sse2) / sse1 < t) {
							improvement = false;
							// since improvement = false, the algorithm has no change in SSE value
							// implying the points are appropriately clustered 
							// so now is when we find the pairwise measures and calculate the external validation
							findPairwiseMeasures();
							double rand = randStatistic();
							double jaccard = jaccardCoefficient();
							double fm = fowlkesMallows();
							if (sse2 < bestFinalSSE) {
								bestFinalSSE = sse2;
							}
							if (rand > bestRand) {
								bestRand = rand;
							}
							if (jaccard > bestJaccard) {
								bestJaccard = jaccard;
							}
							if (fm > bestFM) {
								bestFM = fm;
							}
						}
					}
					calculateCentroids();	// calculate centroids based on mean
					counter++;	// increment counter
				}
			}
			
			myOutfile.write("\nBest Final SSE = " + bestFinalSSE);
			myOutfile.write("\nBest Rand = " + bestRand);
			myOutfile.write("\nBest Jaccard = " + bestJaccard);
			myOutfile.write("\nBest Fowlkes-Mallows = " + bestFM);
			myOutfile.flush();
			myOutfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not save results.");
		}
	}

	
}
