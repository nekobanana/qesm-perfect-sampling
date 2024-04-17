package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.DumbSampler;
import org.example.model.PerfectSamplerCFTP;
import org.example.model.PerfectSamplerForward;
import org.example.model.RunResult;
import org.jetbrains.annotations.NotNull;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static Map<Integer, Double> getSteadyStateDistributionLinearSystem(double[][] P) {
        MutableValueGraph<Object, Double> graph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        Matrix matP = Matrix.from2DArray(P);
        assert(matP.rows() == matP.columns());
        int n = matP.rows();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph.putEdgeValue(i, j, matP.get(i, j));
            }
        }
        return DTMCStationary.builder().build().apply(graph)
                .entrySet().stream()
                .collect(Collectors.toMap(e -> (int)e.getKey(), Map.Entry::getValue));
    }


    public static void main(String[] args) {
        double[][] P = new double[][]{
                new double[]{0, 0.5, 0.25, 0.25},
                new double[]{0.25, 0, 0.5, 0.25},
                new double[]{0.25, 0.25, 0, 0.5},
                new double[]{0.5, 0.25, 0.25, 0},
        };

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

        final int runs = 10000;
        List<RunResult> resultsCFTP = new ArrayList<>();
        PerfectSamplerCFTP samplerCFTP = new PerfectSamplerCFTP(Matrix.from2DArray(P));
        RunResult resultCFTP;

        for (int i = 0; i < runs; i++) {
            samplerCFTP.reset();
            resultCFTP = samplerCFTP.runUntilCoalescence();
            resultsCFTP.add(resultCFTP);
        }

        // Perfect sampling CFTP

        System.out.println("\nPerfect sampling (CFTP)");
        Map<Integer, Long> piCFTP = getDistrFromResults(resultsCFTP, RunResult::getSampledState);
        piCFTP.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));
        float avgStepsCFTP = (float) resultsCFTP.stream().mapToInt(RunResult::getSteps).sum() / runs;
        System.out.println("Avg. steps: " + avgStepsCFTP);
        Map<Integer, Long> histCFTP = getDistrFromResults(resultsCFTP, RunResult::getSteps);
        histCFTP.forEach((state, count) -> System.out.println("steps: " + state + ", count: " + count));
        //        results.forEach(System.out::println);

        try {
            samplerCFTP.writeSequenceToFile("postprocess/output_seq.json");
            BufferedWriter writer = new BufferedWriter(new FileWriter("postprocess/results.json"));
            writer.write((new ObjectMapper()).writeValueAsString(resultsCFTP));
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Perfect sampling forward

        List<RunResult> resultsF = new ArrayList<>();
        PerfectSamplerForward samplerF = new PerfectSamplerForward(Matrix.from2DArray(P));
        RunResult resultF;

        for (int i = 0; i < runs; i++) {
            samplerF.reset();
            resultF = samplerF.runUntilCoalescence();
            resultsF.add(resultF);
        }

        System.out.println("\nPerfect sampling (forward)");
        Map<Integer, Long> piF = getDistrFromResults(resultsF, RunResult::getSampledState);
        piF.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));
        float avgStepsF = (float) resultsF.stream().mapToInt(RunResult::getSteps).sum() / runs;
        System.out.println("Avg. steps: " + avgStepsF);
        Map<Integer, Long> histF = getDistrFromResults(resultsF, RunResult::getSteps);
        histF.forEach((state, count) -> System.out.println("steps: " + state + ", count: " + count));
        try {
            samplerF.writeSequenceToFile("postprocess/output_seq_f.json");
            BufferedWriter writer = new BufferedWriter(new FileWriter("postprocess/results_f.json"));
            writer.write((new ObjectMapper()).writeValueAsString(resultsF));
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DumbSampler dumbSampler = new DumbSampler(Matrix.from2DArray(P));
        List<Integer> resultsD = new ArrayList<>();
        int resultD;
        int initialState = 0;
        int stepsD = (int)(Math.min(avgStepsCFTP, avgStepsF)) + 1;
        for (int i = 0; i < runs; i++) {
            dumbSampler.reset();
            initialState = (initialState + 1) % P.length;
            resultD = dumbSampler.runForNSteps(initialState, stepsD);
            resultsD.add(resultD);
        }
        System.out.println("\nDumb sampling");
        System.out.println("Running for " + stepsD + " steps");
        Map<Integer, Long> pi2 = getDistrFromResults(resultsD, Function.identity());
        pi2.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));

    }

    private static <T> @NotNull Map<Integer, Long> getDistrFromResults(List<T> results, Function<? super T, Integer>  function) {
        return results.stream().collect(Collectors.groupingBy(function, Collectors.counting()));
    }

}