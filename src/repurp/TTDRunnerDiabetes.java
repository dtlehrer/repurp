package repurp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TTDRunnerDiabetes {

	public static void main(String args[]) throws IOException {
		Variables.originalDisease = "diabetes";
		File file = new File("../../input/TTDdata7.csv");
		File proteinsfile = new File("../../output/proteins/diabetesproteinfile.out");
		File symptomsfile = new File("../../input/DiseaseSimilarities.csv");
		Scanner scanf = new Scanner(file);
		Scanner scanf2 = new Scanner(proteinsfile);
		Scanner scanf3 = new Scanner(symptomsfile);
		File weightfile = new File("../../output/drugWeights/diabetes.out");
		PrintWriter weightOut = new PrintWriter(weightfile);
		Repurp rep = new Repurp(scanf, scanf2, scanf3, weightOut);
		rep.run();
	}
}