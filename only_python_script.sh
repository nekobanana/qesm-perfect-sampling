#!/bin/bash

for i in $(seq 0 26)
do
  INPUT_FILE="postprocess/results/${i}/results.json"
  postprocess/venv/bin/python postprocess/main.py -h "$INPUT_FILE"
done
