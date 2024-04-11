package org.example;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.PerfectSampler;
import org.example.model.RunResult;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Map<Integer, Long> pi = results.stream().collect(Collectors.groupingBy(RunResult::getSampledState, Collectors.counting()));
        pi.forEach((state, count) -> System.out.println("state " + state + ", count: " + (double)count / runs));
        System.out.println("Avg. steps: " + results.stream().mapToInt(RunResult::getSteps).sum() / runs);
        //        results.forEach(System.out::println);
    }
}