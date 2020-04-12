package phase4;

// Joshua Sample
// CSCI 4372
// Phase 4: External Validation

// driver of the program
public class Main {

	public static void main(String[] args) {
		// each argument from the command line is instantiated as the following variables
		String f = args[0];
		int k = Integer.parseInt(args[1]);
		int i = Integer.parseInt(args[2]);
		double t = Double.parseDouble(args[3]);
		int r = Integer.parseInt(args[4]);
		
		// create a kMeans object which will run the algorithm
		KMeans cluster = new KMeans(f, k, i, t, r);
		cluster.kMeans();
	}

}
