package org.qesm.app.model.sampling.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.qesm.app.model.sampling.sampler.ForwardSampler;
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
import java.util.stream.Collectors;

public class ForwardCouplingRunner {
    private ForwardSampler sampler;
    private List<RunResult> results = new ArrayList<>();
    private Float avgSteps;
    private Double stdDevSteps;

    private static final String postprocDirPath = "postprocess/";
    private static final String outputDirPath = postprocDirPath + "results/";
    private List<Process> outputWriteProcesses = new ArrayList<>();

    public ForwardCouplingRunner(ForwardSampler sampler) {
        this.sampler = sampler;
    }

    public void run(int runs) {
        avgSteps = null;
        stdDevSteps = null;
        for (int i = 0; i < runs; i++) {
            sampler.reset();
            results.add(sampler.runUntilCoalescence());
        }
    }

    public void writeResultsOutput(String dirName) throws IOException {
        Files.createDirectories(Paths.get(outputDirPath + dirName));
        String outputFileName = outputDirPath + dirName + "/results_forward.json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        writer.write((new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(results));
        writer.close();
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
    public int getNRuns() {
        return results.size();
    }
}
