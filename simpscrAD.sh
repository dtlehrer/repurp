#!/bin/bash
echo "Searching for Alzheimer disease..."
cd src/repurp
javac -d ../../bin -cp .:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar *.java */*/*/*/*.java */*/*/*/*/*.java
java -cp ../../bin:./Lang3/commons-lang3-3.5/commons-lang3-3.5.jar repurp.TTDRunnerAD Alzheimer disease
