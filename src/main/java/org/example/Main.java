package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.DumbSampler;
import org.example.model.PerfectSampler;
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
        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

        List<RunResult> results = new ArrayList<>();
        int runs = 10000;

        for (int i = 0; i < runs; i++) {
            PerfectSampler sampler = new PerfectSampler(Matrix.from2DArray(P));
            RunResult result = sampler.runUntilCoalescence();
            results.add(result);
        }
        Map<Integer, Long> pi = getDistrFromResults(results, RunResult::getSampledState);
        pi.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));
        float avgSteps = (float) results.stream().mapToInt(RunResult::getSteps).sum() / runs;
        System.out.println("Avg. steps: " + avgSteps);
        Map<Integer, Long> hist = getDistrFromResults(results, RunResult::getSteps);
        hist.forEach((state, count) -> System.out.println("steps: " + state + ", count: " + count));
        //        results.forEach(System.out::println);

        // Another sample only for the drawing
        PerfectSampler sampler = new PerfectSampler(Matrix.from2DArray(P));
        sampler.runUntilCoalescence();
        try {
            sampler.writeSequenceToFile("postprocess/output_seq.json");
            BufferedWriter writer = new BufferedWriter(new FileWriter("postprocess/results.json"));
            writer.write((new ObjectMapper()).writeValueAsString(results));
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Integer> resultsDumb = new ArrayList<>();

        int initialState = 0;
        for (int i = 0; i < runs; i++) {
            DumbSampler dumbSampler = new DumbSampler(Matrix.from2DArray(P));
            initialState = (initialState + 1) % P.length;
            int result = dumbSampler.runForNSteps(initialState, (int)avgSteps + 1);
            resultsDumb.add(result);
        }
        System.out.println("Running for " + ((int)avgSteps + 1) + " steps");
        Map<Integer, Long> pi2 = getDistrFromResults(resultsDumb, Function.identity());
        pi2.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));

    }

    private static <T> @NotNull Map<Integer, Long> getDistrFromResults(List<T> results, Function<? super T, Integer>  function) {
        Map<Integer, Long> pi = results.stream().collect(Collectors.groupingBy(function, Collectors.counting()));
        return pi;
    }

}