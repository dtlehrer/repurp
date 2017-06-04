package repurp;

import java.io.*;
import java.util.*;
import repurp.org.apache.commons.exec.CommandLine;
import repurp.org.apache.commons.exec.DefaultExecutor;

public class Repurp {
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
  
  public Repurp(Scanner sc, Scanner scprot, Scanner scsympt, PrintWriter pw){
    this.sc = sc;
    this.scprot = scprot;
    this.scsympt = scsympt;
    this.pw = pw;
  }
  
  public static double entrezQueryDiseaseOnly(){
    String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " +Variables.originalDisease+"\"";
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("../edirect"));
    try{
      executor.execute(commandLine);
    }
    catch (Exception e){
    }
    return (double) Variables.queryCount;
  }
 
  public static double entrezQuery1(String otherDisease, double weightDisOnly){
    String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + otherDisease + "\"";
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("../edirect"));
    try{
      executor.execute(commandLine);
    }
    catch (Exception e){
    }
    return (double) Variables.queryCount/weightDisOnly;
  }
  
  public static double getSymScore(String otherDisease, ArrayList<ArrayList<String>> sRecords){
    if (otherDisease.equals("na")){
      System.out.println("..");
    }
    double symScore = 0;
    if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) Variables.originalDisease.toLowerCase(), (CharSequence) otherDisease.toLowerCase()) < new Integer (Variables.originalDisease.length()).doubleValue()/4.0){
      symScore = 1.0;
      return symScore;
    }
    for (ArrayList<String> r : sRecords){
      if(org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) r.get(0).toLowerCase(), (CharSequence) otherDisease.toLowerCase()) < new Integer (r.get(0).toLowerCase().length()).doubleValue()/4.0){
        if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) r.get(1).toLowerCase(), (CharSequence) Variables.originalDisease.toLowerCase()) < new Integer (r.get(1).toLowerCase().length()).doubleValue()/4.0){
          symScore = Double.parseDouble(r.get(2));
          //HERE to check symptoms
          //System.out.println(symScore);
          return symScore;
        }
      }
      else if(org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) r.get(0).toLowerCase(), (CharSequence) Variables.originalDisease.toLowerCase()) < new Integer (r.get(0).toLowerCase().length()).doubleValue()/4.0){
        if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) r.get(1).toLowerCase(), (CharSequence) otherDisease.toLowerCase()) < new Integer (r.get(1).toLowerCase().length()).doubleValue()/4.0){ 
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
    CommandLine commandLine = CommandLine.parse(line);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(1);
    executor.setWorkingDirectory(new File("../edirect"));
    try{
      executor.execute(commandLine);
    }
    catch (Exception e){
    }
    return (double) Variables.queryCount/weightDisOnly;
  }
  
  public void run() throws IOException {
    double disOnlyWeight = entrezQueryDiseaseOnly();
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
          break;
        }
      }
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
    Map <String, HashMap<String, String>> protsToDrugsAndIndications = new HashMap<String, HashMap<String, String>>();
    for(String prot:protList){
      for (int i=1;i<uIdRecordList.size();i++){
        if(uIdRecordList.get(i).get(0).contains(prot)){
          viableRecordList.add(uIdRecordList.get(i));
          potentialDrugList.add(uIdRecordList.get(i).get(8));
          for(String protein:uIdRecordList.get(i).get(0)){
            HashMap<String,String> drugsInds = new HashMap<String,String>();
            if (protsToDrugsAndIndications.containsKey(protein)){
              drugsInds = protsToDrugsAndIndications.get(protein);
              for (int j=0;j<uIdRecordList.get(i).get(8).size();j++){
                if (j>=uIdRecordList.get(i).get(9).size() && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(uIdRecordList.get(i).get(9).size()-1));
                }
                else if (uIdRecordList.get(i).get(8).size() > 0 && uIdRecordList.get(i).get(9).size() > 0){
                  drugsInds.put(uIdRecordList.get(i).get(8).get(j), uIdRecordList.get(i).get(9).get(j));
                }
              }
            }
            else{
              for (int j=0;j<uIdRecordList.get(i).get(8).size();j++){
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
        if (!protWeights.containsKey(protTarg)){
          System.out.println(n++);
          protWeights.put(protTarg,entrezQuery2(record.get(2).get(0), disOnlyWeight));
        }
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
            ind = "na"; //????
            System.out.println("na " + drug);
          }
          if(!indicationToCount.containsKey(ind)){
            indicationToCount.put(ind,entrezQuery1(ind, disOnlyWeight));
          }
          comboWeights.put(drug, indicationToCount.get(ind));
          sympWeights.put(drug, getSymScore(ind, symptomRecords));
        }
      }
    }
    Set<String> allPotentialDrugs = new HashSet<String>();
    for (int i=0;i<potentialDrugList.size();i++){
      for(int j=0;j<potentialDrugList.get(i).size();j++){
        allPotentialDrugs.add(potentialDrugList.get(i).get(j));
      }
    }
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
      for(String drug:drugs){
        if (!drugSet.contains(drug)){
          pw.println(drug +","+ comboWeights.get(drug) +","+ protWeights.get(prt)+","+sympWeights.get(drug)+","+prt+","+protsToDrugsAndIndications.get(prt).get(drug));
          drugSet.add(drug);
        }
      }
    }
    sc.close();
    scprot.close();
    pw.close();
  }
}
