#!/bin/bash
project_folder=project1
rm -r postprocess/results/*
rm files/output/*
rm files/input/*
scp giorgi@192.168.3.47:"${project_folder}"/files/output/* ./files/output
scp giorgi@192.168.3.47:"${project_folder}"/files/input/* ./files/input
scp -r giorgi@192.168.3.47:"${project_folder}"/postprocess/results/* postprocess/results/
scp giorgi@192.168.3.47:"${project_folder}"/tables/* tables
