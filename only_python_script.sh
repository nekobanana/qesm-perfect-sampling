#!/bin/bash

NUM_FILES=17

for i in $(seq 0 $(($NUM_FILES)))
do
  INPUT_FILE="postprocess/results/output${i}/results.json"
  postprocess/venv/bin/python postprocess/main.py -h "$INPUT_FILE"
done
