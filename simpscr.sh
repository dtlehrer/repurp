#!/bin/bash
echo -e "Please enter a disease name: " 
read name
echo "Searching for ${name}..."
cd src/edirect
esearch -db protein -query "$name AND human [ORGN]" | efilter -source swissprot | efetch -format gpc | xtract -insd INSDSeq_locus > "../../output/proteins/${name}_proteinfile.out"
esearch -db gene -query "$name AND human [ORGN]" | efetch -format xml | xtract -pattern Gene-commentary_properties -element Gene-commentary_text > "../../output/genes/${name}_genefile.out"
input="../../output/genes/${name}_genefile.out"
while read -r f1 f2; do
esearch -db protein -query "$f1 [GENE] AND human [ORGN]" </dev/null | efilter -source swissprot | efetch -format gpc | xtract -insd INSDSeq_locus >> "../../output/proteins/${name}_proteinfile.out"
done < "$input"
cd ../repurp
javac -d ../../bin -cp .:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar *.java */*/*/*/*.java */*/*/*/*/*.java
java -cp ../../bin:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar repurp.TTDRunner $name
