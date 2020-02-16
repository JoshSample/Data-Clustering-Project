package phase1;

// Joshua Sample
// CSCI 4372
// Phase 1: Basic K-Means

public class Main {

	public static void main(String[] args) {
		String f = args[0];
		int k = Integer.parseInt(args[1]);
		int i = Integer.parseInt(args[2]);
		double t = Double.parseDouble(args[3]);
		int r = Integer.parseInt(args[4]);
		
		KMeans cluster = new KMeans(f, k, i, t, r);
		cluster.kMeans();
	}

}
