#!/bin/bash

semEvalDir=/zhubajie/corpora/SemEval2010/
trainDir=$semEvalDir/training_data
testDir=$semEvalDir/test_data
allFiles="$trainDir/nouns/*.xml $trainDir/verbs/*.xml $testDir/nouns/*.xml $testDir/verbs/*.xml"
cleanFiles="$trainDir/nouns/*.txt $trainDir/verbs/*.txt $testDir/nouns/*.txt $testDir/verbs/*.txt"
stopWordFile=$HOME/devel/S-Space/data/english-stop-words-large.txt 
topWordFile=semeval.2010.top.50k.tokens.txt
numTopWords=50000

jar="target/active-wsi-1.0-jar-with-dependencies.jar"
run="scala -J-Xmx4g -cp target/active-wsi-1.0-jar-with-dependencies.jar"
base="edu.ucla.sspace"

# Step 1: extract the contexts into a one line per context format.  We do this
# for each training file and each test file.
echo "Converting XML to one line per doc"
for xml in $allFiles; do
    newName=`echo $xml | sed "s/xml$/txt/"`
    $run $base.ParseRawSemEval $xml > $newName
done

# Step 2: extract the top N words from all contexts excluding stop words
echo "Computing the top $numTopWords words in the corpus"
cat $cleanFiles | tr -s " " "\n" | sort | uniq -c > $topWordFile.tmp
$run $base.SelectTopWords $stopWordFile $numTopWords $topWordFile.tmp $numTopWords > $topWordFile
rm $topWordFile.tmp

# Step 3: Convert each corpus file into a sparse matrix recording the
# co-occurring content words.
echo "Conveting each word's contexts into vectors using a shared feature space"
for txt in $cleanFiles; do
    newName=`echo $txt | sed "s/txt$/svdlibc_sparse_text.mat/"`
    $run $base.ExtractWordsiContexts $topWordFile $txt $newName
done
