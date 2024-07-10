#!/bin/bash

NUM_FILES=26

for i in $(seq 0 $(($NUM_FILES)))
do
  INPUT_FILE="files/input/input${i}.json"
  OUTPUT_FILE="files/output/output${i}.json"
  
  java -jar target/QESM_perfect_sampling-1.0-SNAPSHOT-jar-with-dependencies.jar -i "$INPUT_FILE" -o "$OUTPUT_FILE"
done
