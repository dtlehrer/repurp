package repurp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TTDRunnerAD {

	public static void main(String args[]) throws IOException {
		Variables.originalDisease = "Alzheimer disease";
		File file = new File("../../input/TTDdata7.csv");
		File proteinsfile = new File("../../output/proteins/ADproteinfile.out");
		File symptomsfile = new File("../../input/DiseaseSimilarities.csv");
		Scanner scanf = new Scanner(file);
		Scanner scanf2 = new Scanner(proteinsfile);
		Scanner scanf3 = new Scanner(symptomsfile);
		File weightfile = new File("../../output/drugWeights/Alzheimer disease.out");
		PrintWriter weightOut = new PrintWriter(weightfile);
		Repurp rep = new Repurp(scanf, scanf2, scanf3, weightOut);
		rep.run();
	}
}