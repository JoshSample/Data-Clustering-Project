package phase3;

import java.io.BufferedReader;
import java.io.FileReader;

// Joshua Sample
// CSCI 4372
// Phase 3: Internal Validation

// driver of the program
public class Main {
	private static String f;	// File name
	private static int i;		// Max iterations
	private static double t;	// Conversion threshold
	private static int r;		// Number of runs
	private static Points[] points;	// Holds the points read from file
	private static int numOfPoints;	// Number of points, obtained from file
	private static int dimensionality;	// Dimensionality of points, obtained from file
	private static int kMax;	// max number of clusters
	private static int kMin = 2;	// minimum number of clusters

	public static void main(String[] args) {
		// each argument from the command line is instantiated as the following variables
		f = args[0];
		i = Integer.parseInt(args[1]);
		t = Double.parseDouble(args[2]);
		r = Integer.parseInt(args[3]);
		readFile();	// read file to obtain data
		// create a kMeans object which will run the algorithm k times to find optimal clusters
		for (int k = kMin; k < kMax; k++) {
			KMeans cluster = new KMeans(f, k, i, t, r, points, numOfPoints, dimensionality, kMax);
			cluster.kMeans();
		}
	}

		// reads file, parsed data is as follows:
		// after the first line, each line is a point and thus stored in the array points[].
		// each attribute in the lines, separated by blanks, are new attributes for a given point
		public static void readFile() {
			BufferedReader lineReader = null;
			try {
				FileReader fr = new FileReader("./" + f);
				lineReader = new BufferedReader(fr);
				String line = null;
				line = lineReader.readLine();
				String[] token = line.split(" ");
				numOfPoints = Integer.parseInt(token[0]);
				dimensionality = Integer.parseInt(token[1]);
				points = new Points[numOfPoints];
				kMax = (int) Math.round(Math.sqrt(numOfPoints/2));	// kMax is calculated by rounding the sqrt of the number of points over 2
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

}
