# repurp: Automated Drug Repurposing Exploration
## Overview
This project is the result of summer research and coursework at the College of Saint Benedict/Saint John's University (CSB/SJU).  By taking disease names as input and outputting top potential pre-approved drugs for treatment, our goal is to create an automated drug repurposing program that does the following:
1. more quickly extracts disease-related data from growing biomedical databases
2. uses interconnected biomedical data to suggest top drugs for repurposing
3. produces comparable results to [manual drug repurposing studies](./KeyResources/Zhang_OmicsDataMining) 
4. reduces drug discovery costs
## Downloaded Biomedical Data
### Therapeutic Target Database (TTD) Datasets
Drug project and protein target information was downloaded from the [TTD data download page](http://bidd.nus.edu.sg/BIDD-Databases/TTD/TTD_Download.asp), as seen in the image below:

![TTD Downloaded Files](https://cloud.githubusercontent.com/assets/19999194/26797053/3a4b79be-49f1-11e7-8a77-680d9b488521.PNG)

Highlighted links indicate downloaded text files.  These files were opened within Microsoft Excel, preprocessed, and joined based on shared attributes to create a [CSV file](./input/TTDdata7.csv) accessed within the repurposing prototype.
### Human Symptoms-Disease Network (HSDN) Symptom Similarity Scores
133,106 symptom similarity scores between 1,596 distinct diseases were downloaded in a [2014 study](https://www.nature.com/articles/ncomms5212) dataset ([Supplementary Data 4](https://www.nature.com/article-assets/npg/ncomms/2014/140626/ncomms5212/extref/ncomms5212-s5.txt)).  This dataset was re-formatted to a [CSV file](./input/DiseaseSimilarities.csv); it is used to generate one of the drug weights implemented in ranking drug suggestions within the repurposing prototype. 
## Getting Started

## Academic Work
[Paper](Paper/Lehrer_SOTF.pdf)

[Presentation](https://docs.google.com/presentation/d/1cVauG0fB8b0WDdLQA0-m_aDHLD72X3S9NsO3HkGydaQ/edit?usp=sharing)

[Original Prototype Demonstration Video (slightly outdated...file structure differs from this repo)](https://www.youtube.com/watch?v=CcGqaZKLo1s)
