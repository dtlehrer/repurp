package repurp;
import java.io.*;
import java.util.*;

public class TTDRunner {
  
  public static void main(String args[]) throws IOException{
    String s = "";
    for(int i=0; i<args.length;i++){
      if (i<args.length - 1){
        s= s + args[i] + " ";
      }
      else{
        s = s + args[i];
      }
    }
    Variables.originalDisease = s;
    File file = new File("../../input/TTDdata7.csv");
    File proteinsfile = new File("../../output/proteins/"+ Variables.originalDisease + "_proteinfile.out");
    File symptomsfile = new File("../../input/DiseaseSimilarities.csv");
    Scanner scanf = new Scanner(file);
    Scanner scanf2 = new Scanner(proteinsfile);
    Scanner scanf3 = new Scanner(symptomsfile);
    File weightfile = new File("../../output/drugWeights/"+Variables.originalDisease+".out");
    PrintWriter weightOut = new PrintWriter(weightfile);
    Repurp rep = new Repurp(scanf, scanf2, scanf3, weightOut);
    rep.run();
  }
}