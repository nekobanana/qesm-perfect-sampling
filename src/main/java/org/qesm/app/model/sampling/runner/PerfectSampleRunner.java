package org.qesm.app.model.sampling.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.qesm.app.model.sampling.sampler.PerfectSampler;
import org.qesm.app.model.sampling.sampler.RunResult;
import org.qesm.app.model.test.StatisticalTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PerfectSampleRunner implements SamplerRunner {
    private PerfectSampler sampler;
    private List<RunResult> results = new ArrayList<>();
    private Float avgSteps;
    private Double stdDevSteps;
    private int minSamplesNumber = 2;

    private static final String postprocDirPath = "postprocess/";
    private static final String outputDirPath = postprocDirPath + "results/";
    private List<Process> outputWriteProcesses = new ArrayList<>();

    public PerfectSampleRunner(PerfectSampler sampler) {
        this.sampler = sampler;
    }

    @Override
    public void run(int runs) {
        avgSteps = null;
        stdDevSteps = null;
        for (int i = 0; i < runs; i++) {
            sampler.reset();
            results.add(sampler.runUntilCoalescence());
        }
    }

    public int run(StatisticalTest stopConditionTest) {
        avgSteps = null;
        stdDevSteps = null;
        for (int i = 0; i < minSamplesNumber; i++) {
            sampler.reset();
            RunResult result = sampler.runUntilCoalescence();
            results.add(result);
            stopConditionTest.addNewSample(result.getSteps());
        }
        do {
            sampler.reset();
            RunResult result = sampler.runUntilCoalescence();
            results.add(result);
            stopConditionTest.addNewSample(result.getSteps());
        } while (!stopConditionTest.test());
        return results.size();
    }

    @Override
    public Map<Integer, Double> getStatesDistribution() {return getStatesDistribution(false);}
    @Override
    public Map<Integer, Double> getStatesDistribution(boolean print) {
        Map<Integer, Double> pi = SamplerRunner.getDistrFromResults(results, RunResult::getSampledState)
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e ->  (double)e.getValue() / results.size()));
        if (print) {
            System.out.println("\nPerfect sampling");
            pi.forEach((state, count) ->
                    System.out.println("state " + state + ": " + count));
        }
        return pi;
    }

    public Map<Integer, Long> getStepsDistribution() {return getStepsDistribution(false);}
    public Map<Integer, Long> getStepsDistribution(boolean print) {
        Map<Integer, Long> hist = SamplerRunner.getDistrFromResults(results, RunResult::getSteps);
        if (print) {
            System.out.println("\nPerfect sampling");
            hist.forEach((state, count) ->
                    System.out.println("steps: " + state + ", count: " + count));
        }
        return hist;
    }

    public Float getAvgSteps() {
            avgSteps = (float) results.stream().mapToInt(RunResult::getSteps).sum() / results.size();
        return avgSteps;
    }

    public Double getStdDevSteps() {
            stdDevSteps = Math.sqrt(results.stream()
                    .mapToDouble(r -> Math.pow(r.getSteps() - avgSteps, 2)).sum() / (results.size() - 1));
        return stdDevSteps;
    }

    public void writeSequenceOutput(String dirName) throws IOException {
        Files.createDirectories(Paths.get(outputDirPath + dirName));
        String outputFileName = outputDirPath + dirName + "/last_seq.json";
        sampler.writeSequenceToFile(outputFileName);
    }

    public void writeResultsOutput(String dirName) throws IOException {
        Files.createDirectories(Paths.get(outputDirPath + dirName));
        String outputFileName = outputDirPath + dirName + "/results.json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        writer.write((new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(results));
        writer.close();
    }



    public int getNRuns() {
        return results.size();
    }
}
