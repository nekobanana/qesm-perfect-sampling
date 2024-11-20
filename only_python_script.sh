#!/bin/bash

for i in $(seq 0 28)
do
  INPUT_FILE="postprocess/results/${i}/results_forward_coupling.json"
  postprocess/venv/bin/python postprocess/main.py -h "$INPUT_FILE"
done
