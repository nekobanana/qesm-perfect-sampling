package org.qesm.app.model.sampling.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.qesm.app.model.sampling.sampler.PerfectSampler;
import org.qesm.app.model.sampling.sampler.RunResult;
import org.qesm.app.model.test.StatisticalTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PerfectSampleRunner implements SamplerRunner {
    PerfectSampler sampler;
    List<RunResult> results = new ArrayList<>();
    Float avgSteps;
    Double stdDevSteps;

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
        for (int i = 0; i < 100; i++) { // at least 10 samples
            sampler.reset();
            results.add(sampler.runUntilCoalescence());
        }
        do {
            sampler.reset();
            results.add(sampler.runUntilCoalescence());
            stopConditionTest.updateStats(results.size(), getAvgSteps(), getStdDevSteps());
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

    public int getAvgStepsPlusStdDev(int sigmaCount) {
        return (int) Math.round(getAvgSteps() + sigmaCount * getStdDevSteps());
    }

    public Float getAvgSteps() {
        if (avgSteps == null) {
            avgSteps = (float) results.stream().mapToInt(RunResult::getSteps).sum() / results.size();
        }
        return avgSteps;
    }

    public Double getStdDevSteps() {
        if (stdDevSteps == null) {
            stdDevSteps = Math.sqrt(results.stream()
                    .mapToDouble((r) -> Math.pow(r.getSteps() - avgSteps, 2)).sum()) / avgSteps;
        }
        return stdDevSteps;
    }

    public void writeSequenceOutput(String fileName) throws IOException {
        Files.createDirectories(Paths.get("postprocess/results/" + fileName));
        sampler.writeSequenceToFile("postprocess/results/" + fileName + "/last_seq.json");
    }

    public void writeResultsOutput(String fileName) throws IOException {
        Files.createDirectories(Paths.get("postprocess/results/" + fileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter("postprocess/results/" + fileName + "/results.json"));
        writer.write((new ObjectMapper()).writeValueAsString(results));
        writer.close();
    }
}
