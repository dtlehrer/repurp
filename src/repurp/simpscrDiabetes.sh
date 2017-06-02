#!/bin/bash
echo -e "Please enter a disease name: " 
read name
echo "Searching for $name..."
cd ~
cd edirect
#esearch -db protein -query "$name AND human [ORGN]" | efilter -source swissprot | efetch -format gpc | xtract -insd INSDSeq_locus > proteinfile.out
#input="/home/dtlehrer/edirect/genefile.out"
#while read -r f1 f2; do
#esearch -db gene -query "$name AND human [ORGN]" | efetch -format xml | xtract -pattern Gene-commentary_properties -element Gene-commentary_text > genefile.out
#input="/home/dtlehrer/edirect/genefile.out"
#while read -r f1 f2; do
#esearch -db protein -query "$f1 [GENE] AND human [ORGN]" </dev/null | efilter -source swissprot | efetch -format gpc | xtract -insd INSDSeq_locus >> proteinfile.out
#done < "$input"
cd ~
cd Thesis
javac -cp .:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar  *.java
java -cp .:./Thesis:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar Thesis.TTDRunnerDiabetes diabetes
#cd ~
#cd Desktop
#libreoffice --calc ./$name.out
