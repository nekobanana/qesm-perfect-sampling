#!/bin/bash
timestamp=$(date "+%Y%m%d-%H%M%S")
mkdir results_history/"${timestamp}"
cp -r files results_history/"${timestamp}"
cp -r postprocess/results results_history/"${timestamp}"/results
cp -r tables results_history/"${timestamp}"