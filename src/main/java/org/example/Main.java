package org.example;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.PerfectSampler;
import org.example.model.RunResult;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

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

        PerfectSampler sampler = new PerfectSampler(Matrix.from2DArray(P));
        RunResult result = sampler.runUntilCoalescence();
        System.out.println("Sampled state: " +  result.getSampledState() + "\t Steps: " + result.getSteps());
    }
}