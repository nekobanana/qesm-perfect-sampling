#!/bin/bash
INPUT_FILES_DIR=files/input
OUTPUT_FILES_DIR=files/output
TABLES_FILES_DIR=tables
POST_PROCESS_RESULTS_DIR=postprocess/results
PYTHON_VENV=postprocess/venv/bin/python
JAR_FILE=target/QESM_perfect_sampling-1.0-SNAPSHOT-jar-with-dependencies.jar
for input_file in "${INPUT_FILES_DIR}"/*; do
    if [ -f "$input_file" ]; then
      filename=$(basename -- "$input_file")
      extension="${filename##*.}"
      filename="${filename%.*}"
      output_file="${OUTPUT_FILES_DIR}/${filename}.${extension}"
      java -jar "$JAR_FILE" -i "$input_file" -o "$output_file"
      echo "${PYTHON_VENV}" postprocess/main.py -h "${POST_PROCESS_RESULTS_DIR}/${filename}/results.json" -s "${POST_PROCESS_RESULTS_DIR}/${filename}/last_seq.json"
      "${PYTHON_VENV}" postprocess/main.py -h "${POST_PROCESS_RESULTS_DIR}/${filename}/results.json" -s "${POST_PROCESS_RESULTS_DIR}/${filename}/last_seq.json"
    fi
done
"${PYTHON_VENV}" postprocess/tables.py -t "${OUTPUT_FILES_DIR};${TABLES_FILES_DIR}/table_1_1.csv;${TABLES_FILES_DIR}/table_1_2.csv;../${TABLES_FILES_DIR}/table_perfect_dumb.csv"



