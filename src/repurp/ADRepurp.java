package repurp;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.commons.lang3.*;

import repurp.org.apache.commons.exec.CommandLine;
import repurp.org.apache.commons.exec.DefaultExecutor;

public class ADRepurp {
  Scanner sc;
  Scanner scprot;
  Scanner scsympt;
  Scanner scgene;
  PrintWriter pw;
  String line;
  int count = 0;
  
  String uniprotId;
  String ttdTargName;
  String ttdTargInd;
  String icd9;
  String icd10;
  String targType;
  String ttdDrugId;
  String drugLNM;
  String ttdIndication;
  ArrayList<ArrayList<ArrayList<String>>> uIdRecordList = new ArrayList<ArrayList<ArrayList<String>>>();
  ArrayList<ArrayList<String>> uIdRecord = new ArrayList<ArrayList<String>>();
  
  
  public ADRepurp(Scanner scprot, Scanner scgene, PrintWriter pw){
    this.scprot = scprot;
    this.scgene = scgene;
    this.pw = pw;
  }
  
  public ADRepurp(Scanner sc, Scanner scprot, Scanner scsympt, PrintWriter pw){
    //public Repurp(ArrayList<String> uIds, String ttdTargId, String ttdTargInd, String icd9, String icd10, String ttdTargetType, ArrayList<String> ttdDrugIds, ArrayList<String> drugLNMs, ArrayList<String> ttdIndications){
    this.sc = sc;
    this.scprot = scprot;
    this.scsympt = scsympt;
    this.pw = pw;
  }
  
  public void genes2Proteins() throws IOException{
    while (scgene.hasNextLine()){
      line = scgene.nextLine();
      String gene = "";
      for (int i=0;i<line.length();i++){
        if (line.charAt(i) != '\t'){
          gene = gene + line.charAt(i);
          entrezGeneToProteinsQuery(gene);
        }
        else{
          System.out.println(gene);
          break;
        }
      }
    }
  }
  
// public static String runCommandForOutput(List<String> params) {
//    ProcessBuilder pb = new ProcessBuilder(params);
//    Process p;
//    String result = "";
//    try {
//        p = pb.start();
//        final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//        StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
//        reader.lines().iterator().forEachRemaining(sj::add);
//        result = sj.toString();
//
//        p.waitFor();
//        p.destroy();
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//    return result;
//}
  
  
  public static double entrezGeneToProteinsQuery(String gene){
    //String line = "esearch -db protein -query \"" + gene + " [GENE] AND human [ORGN]\"";
    String line = "esearch -db protein -query \"" + gene + "\"";
    System.out.println(line);
    Variables.fullQuery = true;
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("/net/home/dtlehrer/edirect"));
    try{
      int exitValue = executor.execute(commandLine);
    }
    catch (Exception e){
    }
    //System.out.println(Variables.queryCount);
    return (double) Variables.queryCount;
  }
  
  public static double entrezQueryDiseaseOnly(){
    String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " +Variables.originalDisease+"\"";
    //System.out.println(line);
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("/net/home/dtlehrer/edirect"));
    try{
      int exitValue = executor.execute(commandLine);
    }
    catch (Exception e){
    }
    //System.out.println(Variables.queryCount);
    return (double) Variables.queryCount;
  }
  
  
  
  public static double entrezQuery1(String otherDisease, double weightDisOnly){
    String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + otherDisease + "\"";
    //String line = "esearch -db pubmed -query \"meningitis and diabetes\"";
    //System.out.println(line);
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("/net/home/dtlehrer/edirect"));
    try{
      int exitValue = executor.execute(commandLine);
    }
    catch (Exception e){
    }
    return (double) Variables.queryCount/weightDisOnly;
    //return (double) Variables.queryCount;
  }
  
  public static double getSymScore(String otherDisease, ArrayList<ArrayList<String>> sRecords){
    if (otherDisease.equals("na")){
      System.out.println("..");
    }
    double symScore = 0;
    if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) Variables.originalDisease.toLowerCase(), (CharSequence) otherDisease.toLowerCase()) < new Integer (Variables.originalDisease.length()).doubleValue()/4.0){
      symScore = 1.0;
      System.out.println("match: " + Variables.originalDisease + " " + otherDisease);
    }
    for (ArrayList<String> r : sRecords){
      
      if(r.get(0).toLowerCase().contains(otherDisease.toLowerCase())){
        if (r.get(1).toLowerCase().contains(Variables.originalDisease.toLowerCase())){
          symScore = Double.parseDouble(r.get(2));
          //HERE to check symptoms
          //System.out.println(symScore);
          return symScore;
        }
      }
      else if (r.get(0).toLowerCase().contains(Variables.originalDisease.toLowerCase())){
        //System.out.println(r.get(0) + "/" + otherDisease + "/" + Variables.originalDisease);
        if (r.get(1).toLowerCase().contains(otherDisease.toLowerCase())){
          symScore = Double.parseDouble(r.get(2));
          //HERE to check symptoms
          //System.out.println(symScore);
          return symScore;
        }
      }
    }
    return symScore;
  }
  
  public static double entrezQuery2(String protein, double weightDisOnly){
    String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + protein + "\"";
    //System.out.println(line);
    //String line = "esearch -db pubmed -query \"meningitis and diabetes\"";
    //System.out.println(line);
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("/net/home/dtlehrer/edirect"));
    try{
      int exitValue = executor.execute(commandLine);
    }
    catch (Exception e){
    }
    //System.out.println( (double) Variables.queryCount/weightDisOnly);
    return (double) Variables.queryCount/weightDisOnly;
    //return (double) Variables.queryCount;
  }
  
  
// public static String runScript(List<String> params) {
//   System.out.println(params);
//   ProcessBuilder pb = new ProcessBuilder(params);
//   Map<String, String> env = pb.environment();
//   env.put("VAR1", "myValue");
//   env.remove("OTHERVAR");
//   env.put("VAR2", env.get("VAR1") + "suffix");
//   pb.directory(new File("/net/home/dtlehrer/Thesis"));
//   String result = "";
//   String line;
//   try{
//     Process p = pb.start();
//     final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//     System.out.println(input.ready());
//     System.out.println(line = input.readLine());
//     while (input.ready()) {
//       line = input.readLine();
//       System.out.println("xy");
//       System.out.println(line);
//       System.out.println("z");
//       System.out.println(input.ready());
//     }
//     System.out.println("hey");
//     input.close();
//
//   }
//   catch (Exception e){
//     e.printStackTrace();
//   }
//   return result;
// }
  
// public static void runScripto(List<String> params){
//   try
//    {
//     ProcessBuilder pb = new ProcessBuilder(params);
//        final Process p = pb.start();
//        final ProcessResultReader stderr = new ProcessResultReader(p.getErrorStream(), "STDERR");
//        final ProcessResultReader stdout = new ProcessResultReader(p.getInputStream(), "STDOUT");
//        stderr.start();
//        stdout.start();
//        final int exitValue = p.waitFor();
//        if (exitValue == 0)
//        {
//            System.out.print(stdout.toString());
//        }
//        else
//        {
//            System.err.print(stderr.toString());
//        }
//    }
//    catch (final IOException e)
//    {
//        throw new RuntimeException(e);
//    }
//    catch (final InterruptedException e)
//    {
//        throw new RuntimeException(e);
//    }
// }
// 
// public static String execCmd(String cmd, Scanner sc) throws java.io.IOException {
//        sc = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
//        return sc.hasNext() ? sc.next() : "";
//    }
  
  
  public void run() throws IOException {
    //System.out.println(Variables.originalDisease);
    
    //final List<String> commands = new ArrayList<String>();                
//////    commands.add("rfind");
////    Runtime.getRuntime().exec("rfind", null, new File("/net/home/dtlehrer/Thesis"));
//    Scanner s = new Scanner(System.in);;
//    String result = execCmd("pwd", s);
//    s.close();
    //List<String> params = Arrays.asList("/bin/sh", "-c", "rfind");
    //List<String> params = Arrays.asList("/bin/sh", "-c", "esearch", "-db", "pubmed", "-query", "\"meningitis\"");
    //String result = runScript(params);
    //System.out.println(result +"&^gfdghdfgnfdhn");
    //runScripto(params);
    
////    ProcessBuilder pb = new ProcessBuilder(commands);
////    pb.directory(new File("/net/home/dtlehrer"));
////    File outputfile = new File("/net/home/dtlehrer/edirect/outputfile.out");
////    pb.redirectOutput(outputfile);
////    pb.start();
// String[] args = new String[] {"/bin/bash", "-c", "your_command", "with", "args"};
//Process proc = new ProcessBuilder(args).start();
    double disOnlyWeight = entrezQueryDiseaseOnly();
    //System.out.println("DISEASE ONLY: " + disOnlyWeight);
    while(sc.hasNextLine()){
      ArrayList<String> uIds = new ArrayList<String>();
      ArrayList<String> ttdTargInds = new ArrayList<String>();
      ArrayList<String> icd9List = new ArrayList<String>();
      ArrayList<String> icd10List = new ArrayList<String>();
      ArrayList<String> targTypeList = new ArrayList<String>();
      ArrayList<String> ttdDrugIds = new ArrayList<String>();
      ArrayList<String> drugLNMs = new ArrayList<String>();
      ArrayList<String> ttdIndications = new ArrayList<String>();
      //skip first line with headers
      line=sc.nextLine();
      uniprotId="";
      ttdTargInd="";
      icd9="";
      icd10="";
      targType="";
      ttdDrugId="";
      drugLNM="";
      ttdIndication="";
      for(int i=0; i<line.length();i++){
        //get uIDs
        if (line.charAt(i)!=',' && line.charAt(i)!=';' && line.charAt(i)!=' ') {
          uniprotId += line.charAt(i);
        }
        else if(line.charAt(i)==';'){
          uIds.add(uniprotId);
          uniprotId="";
        }
        else if(line.charAt(i)==' '){
        }
        else{ //comma
          uIds.add(uniprotId);
          //end of uniprot id(s)
          i++;
          String ttdTargId="";
          while(i<line.length()){
            if (line.charAt(i)!=',') {
              ttdTargId += line.charAt(i);
            }
            else{ //comma (end of ttdTargId)
              i++; 
              break;
            }
            i++;
          }
          String ttdTargName="";
          while(i<line.length()){
            if (line.charAt(i)!=',') {
              ttdTargName += line.charAt(i);
            }
            else{ //comma (end of ttdTargName)
              i++;
              break;
            }
            i++;
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=';') {
              ttdTargInd += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==';'){ //skip leading semicolon + leading space
              ttdTargInds.add(ttdTargInd);
              ttdTargInd="";
              i++;
              i++;
            }
            else{ //comma
              ttdTargInds.add(ttdTargInd);
              //System.out.println(ttdTargInds);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=';' && line.charAt(i)!=' ') {
              icd9 += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==';'){ //skip leading semicolon + leading space
              icd9List.add(icd9);
              icd9="";
              i++;
              i++;
            }
            else{ //comma
              icd9List.add(icd9);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=';' && line.charAt(i)!=' ') {
              icd10 += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==';'){ //skip leading semicolon + leading space
              icd10List.add(icd10);
              icd10="";
              i++;
              i++;
            }
            else{ //comma
              icd10List.add(icd10);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=';') {
              targType += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==';'){ //skip leading semicolon + leading space
              targTypeList.add(targType);
              targType="";
              i++;
              i++;
            }
            else{ //comma
              targTypeList.add(targType);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=';') {
              ttdDrugId += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==';'){ //skip leading semicolon + leading space
              ttdDrugIds.add(ttdDrugId);
              ttdDrugId="";
              i++;
              i++;
            }
            else{ //comma
              ttdDrugIds.add(ttdDrugId);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=':') {
              drugLNM += line.charAt(i);
              i++;
            }
            else if(line.charAt(i)==':'){ //skip leading colon + next colon + space
              drugLNMs.add(drugLNM);
              drugLNM="";
              i++;
              i++;
              i++;
            }
            else{ //comma
              drugLNMs.add(drugLNM);
              i++;
              break;
            }
          }
          while(i<line.length()){
            if (line.charAt(i)!=',' && line.charAt(i)!=':') {
              ttdIndication += line.charAt(i);
              if(i==line.length()-1){
                ttdIndications.add(ttdIndication);
              }
              i++;
            }
            else if(line.charAt(i)==':'){ //skip leading colon + next colon + space
              ttdIndications.add(ttdIndication);
              ttdIndication="";
              i++;
              i++;
              i++;
            }
            else{ //comma
              ttdIndications.add(ttdIndication);
              i++;
              break;
            }
          }
          uIdRecord = new ArrayList<ArrayList<String>>();
          uIdRecord.add(uIds);
          ArrayList<String> ttdTargIdList = new ArrayList<String>();
          ttdTargIdList.add(ttdTargId);
          uIdRecord.add(ttdTargIdList);
          ArrayList<String> ttdTargNameList = new ArrayList<String>();
          ttdTargNameList.add(ttdTargName);
          uIdRecord.add(ttdTargNameList);
          uIdRecord.add(ttdTargInds);
          uIdRecord.add(icd9List);
          uIdRecord.add(icd10List);
          uIdRecord.add(targTypeList);
          uIdRecord.add(ttdDrugIds);
          uIdRecord.add(drugLNMs);
          uIdRecord.add(ttdIndications);
          uIdRecordList.add(uIdRecord);
//          System.out.println(uIdRecord);
//          System.out.println("------------------------------------");
          break;
        }
      }
      //System.out.println("xxx");
      count++;
    }
    uniprotId="";
    Set<String> protList = new HashSet<String>();
    ArrayList<ArrayList<ArrayList<String>>> viableRecordList = new ArrayList<ArrayList<ArrayList<String>>>();
    ArrayList<ArrayList<String>> potentialDrugList = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> otherDiseaseList = new ArrayList<ArrayList<String>>();
    while(scprot.hasNextLine()){
      line=scprot.nextLine();
      for (int i=0;i<line.length();i++){
        if (line.charAt(i)!='.') {
          uniprotId += line.charAt(i);
        }
        else{
          protList.add(uniprotId);
          uniprotId="";
          break;
        }
      }
      //System.out.println(line);
    }
    String dis1 = "";
    ArrayList<ArrayList<String>> symptomRecords = new ArrayList<ArrayList<String>>();
    while(scsympt.hasNextLine()){
      line=scsympt.nextLine();
      ArrayList<String> record = new ArrayList<String>();
      for (int i=0;i<line.length();i++){
        if (line.charAt(i)!=',' && (i<line.length()-1)){
          dis1 += line.charAt(i);
        }
        else if(i==line.length()-1){
          record.add(dis1);
          symptomRecords.add(record);
          dis1="";
        }
        else{
          record.add(dis1);
          dis1="";
        }
      }
    }
    //Map <String, ArrayList<ArrayList<String>>> protsToDrugsAndIndications = new HashMap<String, ArrayList<ArrayList<String>>>();
    Map <String, HashMap<String, String>> protsToDrugsAndIndications = new HashMap<String, HashMap<String, String>>();
    for(String prot:protList){
      for (int i=1;i<uIdRecordList.size();i++){
        if(uIdRecordList.get(i).get(0).contains(prot)){
          viableRecordList.add(uIdRecordList.get(i));
          potentialDrugList.add(uIdRecordList.get(i).get(8));
          for(String protein:uIdRecordList.get(i).get(0)){
            HashMap<String,String> drugsInds = new HashMap<String,String>();
            if (protsToDrugsAndIndications.containsKey(protein)){
              //ArrayList<ArrayList<String>> b = protsToDrugsAndIndications.get(protein);
              drugsInds = protsToDrugsAndIndications.get(protein);
//             Set<String> drugSet = b.keySet();
//             ArrayList<String> drugs = new ArrayList<String>();
//             ArrayList<String> inds = new ArrayList<String>();
//             for(String dr:drugSet){
//               drugs.add(dr);
//               inds.add(b.get(dr));
//             }
//             for (int i=0; i<uIdRecordList.get(i).size()){
//               drugs.add(uIdRecordList.get(i).get(8));
//               inds.addAll(uIdRecordList.get(i).get(9));
//             }
              //System.out.println("--------------------------------------------------------------------" + uIdRecordList.get(i).get(8).size());
              //System.out.println(uIdRecordList.get(i).get(8));
              for (int j=0;j<uIdRecordList.get(i).get(8).size();j++){
                //System.out.println(drugs.size());
                if (j>=uIdRecordList.get(i).get(9).size() && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(uIdRecordList.get(i).get(9).size()-1));
                }
                else if (uIdRecordList.get(i).get(8).size() > 0 && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(j));
                }
              }
              //drugs.addAll(uIdRecordList.get(i).get(8));
              //inds.addAll(uIdRecordList.get(i).get(9));
            }
            else{
              for (int j=0;j<uIdRecordList.get(i).get(8).size();j++){
                //System.out.println(drugs.size());
                if (j>=uIdRecordList.get(i).get(9).size() && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(uIdRecordList.get(i).get(9).size()-1));
                }
                else if (uIdRecordList.get(i).get(8).size() > 0 && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(j));
                }
              }
            }
            protsToDrugsAndIndications.put(protein,drugsInds);
          }
          otherDiseaseList.add(uIdRecordList.get(i).get(3));
        }
      }
    }
    
    Map<String,Double> protWeights = new HashMap<String,Double>();
    Map<String,Double> comboWeights = new HashMap<String,Double>();
    Map<String,Double> sympWeights = new HashMap<String,Double>();
    Map<String,Double> indicationToCount = new HashMap<String,Double>();
    int n=0;
    for(ArrayList<ArrayList<String>> record:viableRecordList){
      for(String protTarg:record.get(0)){
        //if protsToDrugsAndIndications.contains
        if (!protWeights.containsKey(protTarg)){
          System.out.println(n++);
          protWeights.put(protTarg,entrezQuery2(record.get(2).get(0), disOnlyWeight));
        }
        //System.out.println(protWeights);
        //System.out.println(record.get(0));
        //System.out.println(record.get(8).size() + " " + record.get(9).size());
        for (int i=0; i<record.get(8).size();i++){
          String drug = record.get(8).get(i);
          String ind = "";
          if (i>=record.get(9).size() && record.get(9).size()>0){
            ind = record.get(9).get(record.get(9).size()-1);
          }
          else if (record.get(9).size()>0){
            ind = record.get(9).get(i);
          }
          else{
            ind = "na";
            System.out.println("na " + drug);
          }
          if(!indicationToCount.containsKey(ind)){
            indicationToCount.put(ind,entrezQuery1(ind, disOnlyWeight));
          }
          comboWeights.put(drug, indicationToCount.get(ind));
          sympWeights.put(drug, getSymScore(ind, symptomRecords));
        }
        //comboWeights.put(protTarg,entrezQuery1(record.get(3).get(0)));
      }
    }
    //System.out.println(protWeights.keySet() +"YYYY");
    //System.out.println(protsToDrugsAndIndications.keySet());
    //System.out.println(protWeights);
    //System.out.println(comboWeights);
    //System.out.println(comboWeights.size());
    
    //System.out.println();
    //System.out.println("ODL: " + otherDiseaseList);
    // System.out.println("ODL size: " + otherDiseaseList.size());
    //for(ArrayList<String> otherDisease : otherDiseaseList){
    //System.out.println(otherDisease);
    //System.out.println(entrezQuery1(otherDisease.get(0), disOnlyWeight));
    //}
    //System.out.println("PDL size: " + potentialDrugList.size());
    //System.out.println(viableRecordList);
    //****Map<String,Integer> drugWeight1 = new HashMap<String,Integer>();
    Set<String> allPotentialDrugs = new HashSet<String>();
    for (int i=0;i<potentialDrugList.size();i++){
      for(int j=0;j<potentialDrugList.get(i).size();j++){
        allPotentialDrugs.add(potentialDrugList.get(i).get(j));
      }
    }
    //System.out.println("drug weights: " + drugWeight1);
    //System.out.println();
    System.out.println("Found...");
    if (protList.size()!=1){
      System.out.println("\t" + protList.size() + " related proteins:");
    }
    else {
      System.out.println("\t" + protList.size() + " related protein:");
    }
    System.out.println();
    System.out.println(protList);
    System.out.println();
    Set<ArrayList<String>> potProteinTargetSet = new HashSet<ArrayList<String>>();
    for(ArrayList<ArrayList<String>> vRecord:viableRecordList){
      potProteinTargetSet.add(vRecord.get(0));
    }
    
    if (protsToDrugsAndIndications.size()!=1){
      System.out.println("\t" + protsToDrugsAndIndications.size() + " potential protein targets:");
    }
    else {
      System.out.println("\t" + protsToDrugsAndIndications.size() + " potential protein target:");
    }
    System.out.println();
    System.out.println(protsToDrugsAndIndications.keySet());
    System.out.println();
    System.out.println("\t" + allPotentialDrugs.size() + " potential drugs for repurposing:");
    Set<String> drugSet = new HashSet<String>();
    for(String prt:protsToDrugsAndIndications.keySet()){
      Set<String> drugs = protsToDrugsAndIndications.get(prt).keySet();
      //Collection<String> otherIndications = protsToDrugsAndIndications.get(prt).values();
      for(String drug:drugs){
        if (!drugSet.contains(drug)){
          System.out.println(protsToDrugsAndIndications.get(prt).get(drug)+"!");
          pw.println(drug +","+ comboWeights.get(drug) +","+ protWeights.get(prt)+","+sympWeights.get(drug)+","+prt+","+protsToDrugsAndIndications.get(prt).get(drug)+",test");
          //pw.println(drug +","+ comboWeights.get(drug) +","+ protWeights.get(prt)+","+sympWeights.get(drug) + "," + ((comboWeights.get(drug)*0.5)+(protWeights.get(prt)*0.15)+(sympWeights.get(drug)*0.35)));
          //*****//System.out.print(drug + " (" + comboWeights.get(drug) + ", " +  protWeights.get(prt) + ", " + sympWeights.get(drug) +"), ");
          drugSet.add(drug);
        }
      }
    }
    System.out.println();
    System.out.println();
    //System.out.println(allPotentialDrugs);
    //System.out.println(uIdRecordList.size());
    //System.out.println("end");
    sc.close();
    scprot.close();
    pw.close();
  }
}
