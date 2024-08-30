#!/bin/bash
rm -r postprocess/results/*
rm files/output/*
scp giorgi@192.168.3.47:files/output/* ./files/output
scp -r giorgi@192.168.3.47:postprocess/results/* postprocess/results/
scp giorgi@192.168.3.47:tables/* tables
