# repurp: Automated Drug Repurposing Exploration
## Overview
This project is the result of summer research and coursework at the College of Saint Benedict/Saint John's University (CSB/SJU).  By taking disease names as input and outputting top potential pre-approved drugs for treatment, our goal is to create an automated drug repurposing program that does the following:
1. more quickly extracts disease-related data from growing biomedical databases
2. uses interconnected biomedical data to suggest top drugs for repurposing
3. produces comparable results to [manual drug repurposing studies](./KeyResources/Zhang_OmicsDataMining) 
4. reduces drug discovery costs
## Biomedical Data
### Downloaded Biomedical Data
In the current prototype version, [Therapeutic Target Database (TTD)](./input/TTDdata7.csv) and [Human Symptoms-Disease Network (HSDN)](./input/DiseaseSimilarities.csv) datasets have been manually downloaded and pre-processed before program execution for simplicity (avoiding web scraping and reducing program runtime).  However, pre-processing this data could become cumbersome with database updates, so a modified program implementing total automation may require live data extraction from these sources.   
#### Therapeutic Target Database (TTD) Datasets
Drug project and protein target information was downloaded from the [TTD data download page](http://bidd.nus.edu.sg/BIDD-Databases/TTD/TTD_Download.asp), as seen in the image below:

![TTD Downloaded Files](https://cloud.githubusercontent.com/assets/19999194/26797053/3a4b79be-49f1-11e7-8a77-680d9b488521.PNG)

Highlighted links indicate downloaded text files.  These files were opened within Microsoft Excel, preprocessed, and joined based on shared attributes to create a [CSV file](./input/TTDdata7.csv) accessed within the repurposing prototype.
##### The Merged Dataset ([TTDData7.csv](./input/TTDdata7.csv))
The resulting cumulation of TTD data is found in one of the main, internally-saved datasets accessed within the repurposing prototype.  This joined dataset consists of 3,389 records, each containing 10 attributes/fields, which are outlined below:
* **Uniprot ID:** a universal protein identifier
* **TTDTargetID:** a drug target identifier specific to the TTD
* **Target_Name:** the name of the protein target (protein targeted/bound by a drug project)
* **Target Indication:** the disease a protein target has been acted upon to treat
* **ICD9:** International Statistical Classification of Diseases and Related Health Problems, 9th revision.  This is an international disease identification code.
* **ICD10:** more international disease identification codes (10th revision) 
* **Target Type:** a protein target's development stage (successful, clinical trial, research, etc.)
* **TTDDRUGIDs:** TTTD-specific IDs for one or more drugs that act on the corresponding protein target
* **LNMs:** one or more drug names (corresponding to TTDDRUGIDs order)
* **Indications:** a list with the specific disease each drug project attempts to treat (corresponding to TTDDRUGIDs order)

Record fields may be identical in several locations, but the combination of *TTDTargetID* and *Target Indication* should create a composite primary key for the dataset.
#### Human Symptoms-Disease Network (HSDN) Symptom Similarity Scores
133,106 symptom similarity scores between 1,596 distinct diseases were downloaded from a [2014 study](https://www.nature.com/articles/ncomms5212) dataset ([Supplementary Data 4](https://www.nature.com/article-assets/npg/ncomms/2014/140626/ncomms5212/extref/ncomms5212-s5.txt)).  This dataset was converted to a [CSV file](./input/DiseaseSimilarities.csv), and it is used to generate one of the drug weights implemented in ranking drug suggestions within the repurposing prototype. 
### Other Biomedical Data Sources (National Center for Biotechnology Information (NCBI) & Entrez Databases)
[NCBI's Entrez Databases](https://www.ncbi.nlm.nih.gov/books/NBK3837/#_EntrezHelp_The_Entrez_Databases_) (PubMed, Protein, Gene, etc.) provide a quickly growing amount of biomedical data.  These databases are much larger—and change more rapidly—than sources like TTD and HSDN, so it is more vital to avoid saving extract live information from them and avoid saving local database copies.  
#### Entrez Programming Utilities (E-utilities) and Entrez Direct (EDirect)
[E-utilities](https://www.ncbi.nlm.nih.gov/books/NBK25497/), the NCBI Entrez system's public API, provide access to all Entrez Databases and a stuctured data retrieval mechanism employed by [EDirect](./KeyResources/Entrez_Direct/), a NCBI-provided downloadable package of executables that allow the E-utilities to be called directly from a UNIX command line.  We can use EDirect command line queries in our program to extract a wide range of highly-customized information from databases like PubMed, Protein, and Gene.  See [repurp/KeyResources/Entrez_Direct/](./KeyResources/Entrez_Direct/) or our [paper](./Paper/Lehrer_SOTF.pdf) for more information related to the E-utilities and EDirect.
## Getting Started
This project was built and tested on a Linux machine.  For optimized performance, it is currently recommended that you execute its scripts on a comparable setup.  TTD and HSDN datasets are already downloaded and pre-processed, and EDirect tools have been installed to a [local package](./src/edirect/), so you should be able to download and run this repository's collection of files on your own machine.  The following steps provide one possible way to work towards customized repurposing on your own machine:
1. download this repository's ZIP file
![Download ZIP](https://user-images.githubusercontent.com/19999194/26889229-16749f92-4b73-11e7-9eb6-e61d2f1eaa08.png)
2. extract all files to your own machine's home directory
3. open a terminal window
4. navigate to ~/repurp-master/ (cd ~/repurp-master)
5. execute one of the scripts by entering its file name in the command line (ex. enter "repurp" or "./repurp" in the command line to run the generalized script, "repurpDiabetes" for the diabetes-specific program, or "repurpAD" for Alzheimer's disease)
6. when prompted by the terminal, provide a disease name as input

After completion, program output should be visible in your [repurp-master/output/](./output) directory.  Disease-related [genes](./output/genes), [proteins](./output/proteins), and [weighted drug suggestions](./output/drugWeights) can be obtained for most input diseases provided to the repurp shell script. 

## Related Work
[Paper](./Paper/Lehrer_SOTF.pdf)

[Presentation](https://docs.google.com/presentation/d/1cVauG0fB8b0WDdLQA0-m_aDHLD72X3S9NsO3HkGydaQ/edit?usp=sharing)

[Original Prototype Demonstration Video (slightly outdated...file structure differs from this repo)](https://www.youtube.com/watch?v=CcGqaZKLo1s)
