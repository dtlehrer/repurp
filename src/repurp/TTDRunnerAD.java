package repurp;
import java.io.*;
import java.util.*;
import org.apache.commons.lang3.*;

public class TTDRunnerAD {

 public static void main(String args[]) throws IOException{
  Variables.originalDisease = "Alzheimer disease";
  File file = new File("/net/home/dtlehrer/Thesis/TTDdata7.csv");
  File proteinsfile = new File("/net/home/dtlehrer/edirect/ADproteinfile.out");
  //File genesfile = new File("/net/home/dtlehrer/edirect/genefile.out");
  File symptomsfile = new File("/net/home/dtlehrer/Thesis/DiseaseSimilarities.csv");
  Scanner scanf = new Scanner(file);
  Scanner scanf2 = new Scanner(proteinsfile);
  Scanner scanf3 = new Scanner(symptomsfile);
  File weightfile = new File("/net/home/dtlehrer/Desktop/Alzheimer disease.out");
  PrintWriter weightOut = new PrintWriter(weightfile);
  Repurp rep = new Repurp(scanf, scanf2, scanf3, weightOut);
  rep.run();
 }
}