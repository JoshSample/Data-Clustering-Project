package phase4;

import java.io.BufferedReader;
import java.io.FileReader;

import phase3.Points;

// Joshua Sample
// CSCI 4372
// Phase 4: External Validation

// driver of the program
public class Main {
	private static String f;	// File name
	private static int k;		// Number of true clusters
	private static int i;		// Max iterations
	private static double t;	// Conversion threshold
	private static int r;		// Number of runs
	private static Points[] points;	// Holds the points read from file
	private static int numOfPoints;	// Number of points, obtained from file
	private static int dimensionality;	// Dimensionality of points, obtained from file
	private static Points trueClusters[][];	// true clusters based on the data from the file

	public static void main(String[] args) {
		// each argument from the command line is instantiated as the following variables
		f = args[0];
		i = Integer.parseInt(args[1]);
		t = Double.parseDouble(args[2]);
		r = Integer.parseInt(args[3]);
		readFile();
		
		// create a kMeans object which will run the algorithm
		KMeans cluster = new KMeans(f, k, i, t, r, points, numOfPoints, dimensionality, trueClusters);
		cluster.kMeans();
	}
	
		// reads file, parsed data is as follows:
		// after the first line, each line is a point and thus stored in the array points[].
		// the first line is number of points, number of attributes + 1, and number of true clusters
		// each attribute in the lines, separated by blanks, are new attributes for a given point
		// except for the last one, which is what true cluster the point belongs to
	public static void readFile() {
		BufferedReader lineReader = null;
		try {
			FileReader fr = new FileReader("./" + f);
			lineReader = new BufferedReader(fr);
			String line = null;
			line = lineReader.readLine();
			line = line.trim();
			String[] token = line.split(" ");
			numOfPoints = Integer.parseInt(token[0]);
			dimensionality = Integer.parseInt(token[1]) - 1;
			k = Integer.parseInt(token[2]);
			points = new Points[numOfPoints];
			trueClusters = new Points[k][numOfPoints];
			int a = 0;
			while ((line = lineReader.readLine())!=null) {
				line = line.trim();
				String[] token2 = line.split("\\s+");
				Points temp = new Points();
				int clust = 0;	// index of the true cluster that the point belongs to
				for(int i = 0; i < token2.length; i++) {
					token2[i] = token2[i].trim();
					if (i < token2.length - 1)
						temp.addAttributes(Double.parseDouble(token2[i]));
					else
						clust = Integer.parseInt(token2[i]);
				}
				points[a] = temp;
				trueClusters[clust][a] = points[a];
				a++;
			}
		} catch (Exception e) {
			System.err.println("There was a problem with the file reader.");
		}
	}

}
