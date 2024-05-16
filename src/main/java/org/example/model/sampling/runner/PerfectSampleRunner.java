package org.example.model.sampling.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.sampling.sampler.PerfectSampler;
import org.example.model.sampling.sampler.RunResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public void writeOutputs() throws IOException {
        sampler.writeSequenceToFile("postprocess/output_seq.json");
        BufferedWriter writer = new BufferedWriter(new FileWriter("postprocess/results.json"));
        writer.write((new ObjectMapper()).writeValueAsString(results));
        writer.close();

    }
}
