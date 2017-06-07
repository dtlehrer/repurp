package repurp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import repurp.org.apache.commons.exec.CommandLine;
import repurp.org.apache.commons.exec.DefaultExecutor;

public class Repurp {
	/** A Scanner for the TTD data text file */
	Scanner sc;
	/** A Scanner for the disease-related proteins text file */
	Scanner scprot;
	/**
	 * A Scanner for the Human Symptoms-Disease Network (HSDN) text file linking
	 * disease pairs to symptom similarity scores
	 */
	Scanner scsympt;
	/**
	 * A PrintWriter for the results output file for drug suggestions and their
	 * corresponding weights
	 */
	PrintWriter pw;
	String line;
	// a program progress tracker
	int count = 0;
	/** a universal protein identifier */
	String uniprotId;
	/**
	 * a drug target identifier specific to the Therapeutic Target Database
	 * (TTD)
	 */
	String ttdTargId;
	/**
	 * the name of a protein target (protein targeted/bound by a drug project)
	 */
	String ttdTargName;
	/** the disease a protein target has been acted upon to treat */
	String ttdTargInd;
	/** an international disease identification code, 9th revision */
	String icd9;
	/** international disease identification code, 10th revision */
	String icd10;
	/**
	 * the protein target's development stage (successful, clinical trial,
	 * research, etc.)
	 */
	String targType;
	/**
	 * TTD-specific IDs for one or more drugs that act on the corresponding
	 * protein target
	 */
	String ttdDrugId;
	/** one or more drug names (corresponding to TTDDRUGIDs order) */
	String drugLNM;
	/**
	 * a list of the specific disease each drug project attempts to treat
	 * (corresponding to TTDDRUGIDs order)
	 */
	String ttdIndication;
	ArrayList<ArrayList<ArrayList<String>>> uIdRecordList = new ArrayList<ArrayList<ArrayList<String>>>();
	ArrayList<ArrayList<String>> uIdRecord = new ArrayList<ArrayList<String>>();

	public Repurp(Scanner sc, Scanner scprot, Scanner scsympt, PrintWriter pw) {
		this.sc = sc;
		this.scprot = scprot;
		this.scsympt = scsympt;
		this.pw = pw;
	}

	/**
	 * Searches the PubMed database with only the input-disease name as a
	 * keyword. The number of unique abstracts are returned.
	 * 
	 * @return the number of unique PubMed abstracts obtained by searching the
	 *         PubMed database with the input disease name as a keyword
	 */
	public static double entrezQueryDiseaseOnly() {
		String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + Variables.originalDisease
				+ "\"";
		CommandLine commandLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWorkingDirectory(new File("../edirect"));
		try {
			executor.execute(commandLine);
		} catch (Exception e) {
		}
		return (double) Variables.queryCount;
	}

	/**
	 * Searches the PubMed database with a combination of the input disease name
	 * and the name of a drug's indication as a keyword, then uses these search
	 * results to generate an individual drug weight between 0 and 1 (the number
	 * of results with both the input disease name and the drug's indication as
	 * a keyword divided by the number of results with only the input disease
	 * name as a keyword).
	 * 
	 * @param otherDisease
	 *            the name of a drug's indication (the disease a drug attempts
	 *            to treat)
	 * @param weightDisOnly
	 *            the number of unique PubMed abstracts obtained by searching
	 *            the PubMed database with only the input disease name as a
	 *            keyword
	 * @return the input disease to indication weight for an individual drug
	 *         project: the number of results with both the input disease name
	 *         and the drug's indication as a keyword divided by the number of
	 *         results with only the input disease name as a keyword
	 */
	public static double entrezIndQuery(String otherDisease, double weightDisOnly) {
		String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + otherDisease + "\"";
		CommandLine commandLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWorkingDirectory(new File("../edirect"));
		try {
			executor.execute(commandLine);
		} catch (Exception e) {
		}
		return (double) Variables.queryCount / weightDisOnly;
	}

	/**
	 * Searches the PubMed database with a combination of the input disease name
	 * and the name of a drug's target protein as a keyword, then uses these
	 * search results to generate an individual drug weight between 0 and 1 (the
	 * number of results with both the input disease name and the drug's target
	 * protein as a keyword divided by the number of results with only the input
	 * disease name as a keyword)
	 * 
	 * @param protein
	 *            the name of a drug's target protein (the protein it
	 *            binds/targets to attempt to treat a disease)
	 * @param weightDisOnly
	 *            the number of unique PubMed abstracts obtained by searching
	 *            the PubMed database with only the input disease name as a
	 *            keyword
	 * @return the input disease to target protein weight for an individual drug
	 *         project: the number of results with both the input disease name
	 *         and the drug's target protein as a keyword divided by the number
	 *         of results with only the input disease name as a keyword
	 */
	public static double entrezProtQuery(String protein, double weightDisOnly) {
		String line = "esearch -db pubmed -query \"" + Variables.originalDisease + " AND " + protein + "\"";
		CommandLine commandLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWorkingDirectory(new File("../edirect"));
		try {
			executor.execute(commandLine);
		} catch (Exception e) {
		}
		return (double) Variables.queryCount / weightDisOnly;
	}

	/**
	 * Searches an ArrayList (sRecords) containing records with disease names
	 * and symptom similarity scores for close matches in both the original
	 * input disease and a drug's indication. If both fields match, the a
	 * symptom similarity score (between 0 and 1) is retrieved and returned. A
	 * score of 0 will be given if no close match is found.
	 * 
	 * @param otherDisease
	 *            the name of a drug's indication (the disease a drug attempts
	 *            to treat)
	 * @param sRecords
	 *            an ArrayList containing records with two disease names and a
	 *            corresponding symptom similarity score for each pair. Each
	 *            ArrayList within sRecords has a length of 3, contains a
	 *            disease name in the first two positions, and a symptom
	 *            similarity score between the two respective diseases in the
	 *            final position.
	 * @return a Human Symptoms-Disease Network symptom similarity score for the
	 *         symptoms of the input disease and the symptoms of a drug's
	 *         indication. This score will always fall between 0 and 1 (with 0
	 *         meaning no symptom similarity and 1 indicating complete symptom
	 *         similarity). Higher scores indicate more symptom similarity.
	 */
	public static double getSymScore(String otherDisease, ArrayList<ArrayList<String>> sRecords) {
		if (otherDisease.equals("na")) {
			System.out.println("..");
		}
		double symScore = 0;
		if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance(
				(CharSequence) Variables.originalDisease.toLowerCase(),
				(CharSequence) otherDisease.toLowerCase()) < new Integer(Variables.originalDisease.length())
						.doubleValue() / 4.0) {
			symScore = 1.0;
			return symScore;
		}
		for (ArrayList<String> r : sRecords) {
			if (org.apache.commons.lang3.StringUtils.getLevenshteinDistance((CharSequence) r.get(0).toLowerCase(),
					(CharSequence) otherDisease.toLowerCase()) < new Integer(r.get(0).toLowerCase().length())
							.doubleValue() / 4.0) {
				if (org.apache.commons.lang3.StringUtils
						.getLevenshteinDistance((CharSequence) r.get(1).toLowerCase(),
								(CharSequence) Variables.originalDisease
										.toLowerCase()) < new Integer(r.get(1).toLowerCase().length()).doubleValue()
												/ 4.0) {
					symScore = Double.parseDouble(r.get(2));
					return symScore;
				}
			} else if (org.apache.commons.lang3.StringUtils
					.getLevenshteinDistance((CharSequence) r.get(0).toLowerCase(),
							(CharSequence) Variables.originalDisease
									.toLowerCase()) < new Integer(r.get(0).toLowerCase().length()).doubleValue()
											/ 4.0) {
				if (org.apache.commons.lang3.StringUtils
						.getLevenshteinDistance((CharSequence) r.get(1).toLowerCase(),
								(CharSequence) otherDisease
										.toLowerCase()) < new Integer(r.get(1).toLowerCase().length()).doubleValue()
												/ 4.0) {
					symScore = Double.parseDouble(r.get(2));
					return symScore;
				}
			}
		}
		return symScore;
	}

	public void run() throws IOException {
		double disOnlyWeight = entrezQueryDiseaseOnly();
		while (sc.hasNextLine()) { // while there are still TTDdata7.csv records
									// to be parsed
			ArrayList<String> uIds = new ArrayList<String>();
			ArrayList<String> ttdTargInds = new ArrayList<String>();
			ArrayList<String> icd9List = new ArrayList<String>();
			ArrayList<String> icd10List = new ArrayList<String>();
			ArrayList<String> targTypeList = new ArrayList<String>();
			ArrayList<String> ttdDrugIds = new ArrayList<String>();
			ArrayList<String> drugLNMs = new ArrayList<String>();
			ArrayList<String> ttdIndications = new ArrayList<String>();
			// skip first data line (headers)
			// each "line" is a record from TTDData7.csv
			line = sc.nextLine();
			uniprotId = "";
			ttdTargId = "";
			ttdTargInd = "";
			icd9 = "";
			icd10 = "";
			targType = "";
			ttdDrugId = "";
			drugLNM = "";
			ttdIndication = "";
			// PARSE THE TTD DATA CSV FILE, IDENTIFYING AND STORING ALL FIELDS
			// FOR EACH RECORD
			for (int i = 0; i < line.length(); i++) {
				// get uIDs
				// getUniprotIDs(line, i, uniprotId, uIds);
				if (line.charAt(i) != ',' && line.charAt(i) != ';' && line.charAt(i) != ' ') {
					uniprotId += line.charAt(i);
				} else if (line.charAt(i) == ';') {
					uIds.add(uniprotId);
					uniprotId = "";
				} else if (line.charAt(i) == ' ') {
				} else { // comma
					uIds.add(uniprotId);
					// end of uniprot id(s)
					i++;
					ttdTargId = "";
					while (i < line.length()) {
						if (line.charAt(i) != ',') {
							ttdTargId += line.charAt(i);
						} else { // comma (end of ttdTargId)
							i++;
							break;
						}
						i++;
					}
					String ttdTargName = "";
					while (i < line.length()) {
						if (line.charAt(i) != ',') {
							ttdTargName += line.charAt(i);
						} else { // comma (end of ttdTargName)
							i++;
							break;
						}
						i++;
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ';') {
							ttdTargInd += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ';') { // skip leading
															// semicolon +
															// leading space
							ttdTargInds.add(ttdTargInd);
							ttdTargInd = "";
							i++;
							i++;
						} else { // comma
							ttdTargInds.add(ttdTargInd);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ';' && line.charAt(i) != ' ') {
							icd9 += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ';') { // skip leading
															// semicolon +
															// leading space
							icd9List.add(icd9);
							icd9 = "";
							i++;
							i++;
						} else { // comma
							icd9List.add(icd9);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ';' && line.charAt(i) != ' ') {
							icd10 += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ';') { // skip leading
															// semicolon +
															// leading space
							icd10List.add(icd10);
							icd10 = "";
							i++;
							i++;
						} else { // comma
							icd10List.add(icd10);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ';') {
							targType += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ';') { // skip leading
															// semicolon +
															// leading space
							targTypeList.add(targType);
							targType = "";
							i++;
							i++;
						} else { // comma
							targTypeList.add(targType);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ';') {
							ttdDrugId += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ';') { // skip leading
															// semicolon +
															// leading space
							ttdDrugIds.add(ttdDrugId);
							ttdDrugId = "";
							i++;
							i++;
						} else { // comma
							ttdDrugIds.add(ttdDrugId);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ':') {
							drugLNM += line.charAt(i);
							i++;
						} else if (line.charAt(i) == ':') { // skip leading
															// colon + next
															// colon + space
							drugLNMs.add(drugLNM);
							drugLNM = "";
							i++;
							i++;
							i++;
						} else { // comma
							drugLNMs.add(drugLNM);
							i++;
							break;
						}
					}
					while (i < line.length()) {
						if (line.charAt(i) != ',' && line.charAt(i) != ':') {
							ttdIndication += line.charAt(i);
							if (i == line.length() - 1) {
								ttdIndications.add(ttdIndication);
							}
							i++;
						} else if (line.charAt(i) == ':') { // skip leading
															// colon + next
															// colon + space
							ttdIndications.add(ttdIndication);
							ttdIndication = "";
							i++;
							i++;
							i++;
						} else { // comma
							ttdIndications.add(ttdIndication);
							i++;
							break;
						}
					}
					// individual fields are added to a record object,
					// uIdRecord, of length=10, see below for record
					// details/indexes
					uIdRecord = new ArrayList<ArrayList<String>>();
					uIdRecord.add(uIds); // 0
					ArrayList<String> ttdTargIdList = new ArrayList<String>();
					ttdTargIdList.add(ttdTargId);
					uIdRecord.add(ttdTargIdList); // 1
					ArrayList<String> ttdTargNameList = new ArrayList<String>();
					ttdTargNameList.add(ttdTargName);
					uIdRecord.add(ttdTargNameList); // 2
					uIdRecord.add(ttdTargInds); // 3
					uIdRecord.add(icd9List); // 4
					uIdRecord.add(icd10List); // 5
					uIdRecord.add(targTypeList); // 6
					uIdRecord.add(ttdDrugIds); // 7
					uIdRecord.add(drugLNMs); // 8
					uIdRecord.add(ttdIndications); // 9
					// all records are stored in a list
					uIdRecordList.add(uIdRecord);
					break;
				}
			}
			count++;
		}
		uniprotId = "";
		// all disease-related proteins are stored in a set, protList
		Set<String> protList = new HashSet<String>();
		// TTDdata7.csv records with uniprot IDs matching disease-related
		// proteins are deemed "viable records" and stored in viableRecordList
		// for further accession
		ArrayList<ArrayList<ArrayList<String>>> viableRecordList = new ArrayList<ArrayList<ArrayList<String>>>();
		// drugs targeting disease-related proteins (those in viableRecordList)
		// are stored in potentialDrugList
		ArrayList<ArrayList<String>> potentialDrugList = new ArrayList<ArrayList<String>>();
		// indications (diseases drugs attempt to treat) are stored in
		// otherDiseaseList, in corresponding order to potentialDrugList
		ArrayList<ArrayList<String>> otherDiseaseList = new ArrayList<ArrayList<String>>();
		while (scprot.hasNextLine()) {
			line = scprot.nextLine();
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) != '.') {
					uniprotId += line.charAt(i);
				} else {
					protList.add(uniprotId);
					uniprotId = "";
					break;
				}
			}
		}
		// the name of a disease
		String dis1 = "";
		// a list of all the records in DiseasesSimilarities.csv, a symptom
		// similarity score data file
		ArrayList<ArrayList<String>> symptomRecords = new ArrayList<ArrayList<String>>();
		while (scsympt.hasNextLine()) {
			line = scsympt.nextLine();
			// a single record from the DiseaseSimilarities.csv file, where
			// diseases are the first two fields, and a symptom similarity score
			// for the pair of diseases is the last field
			ArrayList<String> record = new ArrayList<String>();
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) != ',' && (i < line.length() - 1)) {
					dis1 += line.charAt(i);
				} else if (i == line.length() - 1) {
					record.add(dis1);
					symptomRecords.add(record);
					dis1 = "";
				} else {
					record.add(dis1);
					dis1 = "";
				}
			}
		}
		// a map that links protein names to another map, linking drugs to their
		// respective indications (one protein can be targeted by one or more
		// drugs, and each drug is linked to an indication)
		Map<String, HashMap<String, String>> protsToDrugsAndIndications = new HashMap<String, HashMap<String, String>>();
		for (String prot : protList) {
			for (int i = 1; i < uIdRecordList.size(); i++) {
				if (uIdRecordList.get(i).get(0).contains(prot)) {
					viableRecordList.add(uIdRecordList.get(i));
					potentialDrugList.add(uIdRecordList.get(i).get(8));
					for (String protein : uIdRecordList.get(i).get(0)) {
						HashMap<String, String> drugsInds = new HashMap<String, String>();
						if (protsToDrugsAndIndications.containsKey(protein)) {
							drugsInds = protsToDrugsAndIndications.get(protein);
							for (int j = 0; j < uIdRecordList.get(i).get(8).size(); j++) {
								if (j >= uIdRecordList.get(i).get(9).size() && uIdRecordList.get(i).get(9).size() > 0) {
									drugsInds.put(uIdRecordList.get(i).get(8).get(j),
											uIdRecordList.get(i).get(9).get(uIdRecordList.get(i).get(9).size() - 1));
								} else if (uIdRecordList.get(i).get(8).size() > 0
										&& uIdRecordList.get(i).get(9).size() > 0) {
									drugsInds.put(uIdRecordList.get(i).get(8).get(j),
											uIdRecordList.get(i).get(9).get(j));
								}
							}
						} else {
							for (int j = 0; j < uIdRecordList.get(i).get(8).size(); j++) {
								if (j >= uIdRecordList.get(i).get(9).size() && uIdRecordList.get(i).get(9).size() > 0) {
									drugsInds.put(uIdRecordList.get(i).get(8).get(j),
											uIdRecordList.get(i).get(9).get(uIdRecordList.get(i).get(9).size() - 1));
								} else if (uIdRecordList.get(i).get(8).size() > 0
										&& uIdRecordList.get(i).get(9).size() > 0) {
									drugsInds.put(uIdRecordList.get(i).get(8).get(j),
											uIdRecordList.get(i).get(9).get(j));
								}
							}
						}
						protsToDrugsAndIndications.put(protein, drugsInds);
					}
					otherDiseaseList.add(uIdRecordList.get(i).get(3));
				}
			}
		}

		Map<String, Double> protWeights = new HashMap<String, Double>();
		Map<String, Double> comboWeights = new HashMap<String, Double>();
		Map<String, Double> sympWeights = new HashMap<String, Double>();
		Map<String, Double> indicationToCount = new HashMap<String, Double>();
		for (ArrayList<ArrayList<String>> record : viableRecordList) {
			for (String protTarg : record.get(0)) {
				if (!protWeights.containsKey(protTarg)) {
					protWeights.put(protTarg, entrezProtQuery(record.get(2).get(0), disOnlyWeight));
				}
				for (int i = 0; i < record.get(8).size(); i++) {
					String drug = record.get(8).get(i);
					String ind = "";
					if (i >= record.get(9).size() && record.get(9).size() > 0) {
						ind = record.get(9).get(record.get(9).size() - 1);
					} else if (record.get(9).size() > 0) {
						ind = record.get(9).get(i);
					} else {
						ind = "na"; // offset
					}
					if (!indicationToCount.containsKey(ind)) {
						indicationToCount.put(ind, entrezIndQuery(ind, disOnlyWeight));
					}
					comboWeights.put(drug, indicationToCount.get(ind));
					sympWeights.put(drug, getSymScore(ind, symptomRecords));
				}
			}
		}
		Set<String> allPotentialDrugs = new HashSet<String>();
		for (int i = 0; i < potentialDrugList.size(); i++) {
			for (int j = 0; j < potentialDrugList.get(i).size(); j++) {
				allPotentialDrugs.add(potentialDrugList.get(i).get(j));
			}
		}
		System.out.println("Found...");
		if (protList.size() != 1) {
			System.out.println("\t" + protList.size() + " related proteins:");
		} else {
			System.out.println("\t" + protList.size() + " related protein:");
		}
		System.out.println();
		System.out.println(protList);
		System.out.println();
		Set<ArrayList<String>> potProteinTargetSet = new HashSet<ArrayList<String>>();
		for (ArrayList<ArrayList<String>> vRecord : viableRecordList) {
			potProteinTargetSet.add(vRecord.get(0));
		}

		if (protsToDrugsAndIndications.size() != 1) {
			System.out.println("\t" + protsToDrugsAndIndications.size() + " potential protein targets:");
		} else {
			System.out.println("\t" + protsToDrugsAndIndications.size() + " potential protein target:");
		}
		System.out.println();
		System.out.println(protsToDrugsAndIndications.keySet());
		System.out.println();
		System.out.println("\t" + allPotentialDrugs.size() + " potential drugs for repurposing:");
		Set<String> drugSet = new HashSet<String>();
		for (String prt : protsToDrugsAndIndications.keySet()) {
			Set<String> drugs = protsToDrugsAndIndications.get(prt).keySet();
			for (String drug : drugs) {
				if (!drugSet.contains(drug)) {
					// print drugs, their 3 weights in CSV format, and the
					// drug's indication in CSV format to an output file
					pw.println(drug + "," + comboWeights.get(drug) + "," + protWeights.get(prt) + ","
							+ sympWeights.get(drug) + "," + prt + "," + protsToDrugsAndIndications.get(prt).get(drug));
					drugSet.add(drug);
				}
			}
		}
		sc.close();
		scprot.close();
		pw.close();
	}
}
