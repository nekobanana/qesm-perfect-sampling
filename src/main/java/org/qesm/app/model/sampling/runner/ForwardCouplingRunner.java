package org.qesm.app.model.sampling.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.Combinations;
import org.qesm.app.model.sampling.sampler.ForwardCoupler;
import org.qesm.app.model.sampling.sampler.PerfectSampler;
import org.qesm.app.model.sampling.sampler.RunResult;
import org.qesm.app.model.test.StatisticalTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForwardCouplingRunner implements SamplerRunner {
    private ForwardCoupler coupler;
    private List<RunResult> results = new ArrayList<>();
    private Float avgSteps;
    private Double stdDevSteps;

    private static final String postprocDirPath = "postprocess/";
    private static final String outputDirPath = postprocDirPath + "results/";

    public ForwardCouplingRunner(ForwardCoupler coupler) {
        this.coupler = coupler;
    }

    @Override
    public void run(int runs) {
        avgSteps = null;
        stdDevSteps = null;
        Combinations couples = new Combinations(coupler.getN(), 2);
        Iterator<int[]> couplesIt = couples.iterator();
//        for (int i = 0; i < runs; i++) {
        for (int[] pair : couples) {
            coupler.reset();
//            if (!couplesIt.hasNext()) {
//                couplesIt = couples.iterator();
//            }
//            int[] pair=couplesIt.next();
            results.add(coupler.runUntilCoalescence(pair[0], pair[1]));
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

    public int getAvgStepsPlusStdDev(double sigmaCount) {
        return (int) Math.round(getAvgSteps() + sigmaCount * getStdDevSteps());
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

    public void writeResultsOutput(String dirName) throws IOException {
        Files.createDirectories(Paths.get(outputDirPath + dirName));
        String outputFileName = outputDirPath + dirName + "/results_forward_coupling.json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        writer.write((new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(results));
        writer.close();
    }

    public double[] getResultsSteps() {
        return results.stream().mapToDouble(r -> r.getSteps()).toArray();
    }

    public int getNRuns() {
        return results.size();
    }
}
