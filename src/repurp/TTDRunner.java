package repurp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * A generalized runner class called by the repurp shell script.
 * 
 * @author dtlehrer
 *
 */
public class TTDRunner {

	public static void main(String args[]) throws IOException {
		String s = "";
		// merge all arguments to find the user-provided input disease name
		for (int i = 0; i < args.length; i++) {
			if (i < args.length - 1) {
				s = s + args[i] + " ";
			} else {
				s = s + args[i];
			}
		}
		Variables.originalDisease = s;
		// A file containing all pre-processed Therapeutic Target Database (TTD)
		// records
		File file = new File("../../input/TTDdata7.csv");
		// The shell-script generated text file containing all disease-related
		// proteins by UniProt ID, one-per-line
		File proteinsfile = new File("../../output/proteins/" + Variables.originalDisease + "_proteinfile.out");
		// A text file generated from the Human Symptoms-Disease Network (HSDN)
		// containing symptom similarity scores between disease pairs
		File symptomsfile = new File("../../input/DiseaseSimilarities.csv");
		Scanner scanf = new Scanner(file);
		Scanner scanf2 = new Scanner(proteinsfile);
		Scanner scanf3 = new Scanner(symptomsfile);
		// The output file for program results: suggested drugs for repurposing
		// and their three weights
		File weightfile = new File("../../output/drugWeights/" + Variables.originalDisease + ".out");
		PrintWriter weightOut = new PrintWriter(weightfile);
		Repurp rep = new Repurp(scanf, scanf2, scanf3, weightOut);
		rep.run();
	}
}