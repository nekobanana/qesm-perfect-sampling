package org.example;

import org.la4j.Matrix;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        double[][] P = new double[][]{
                new double[]{0, 0.5, 0.25, 0.25},
                new double[]{0.25, 0, 0.5, 0.25},
                new double[]{0.25, 0.25, 0, 0.5},
                new double[]{0.5, 0.25, 0.25, 0},
        };
        Map<Integer, Double> solution = PerfectSampling.getSteadyStateDistributionLinearSystem(P);
        System.out.println(solution);

        StatesTransitionSequence sampler = new StatesTransitionSequence(Matrix.from2DArray(P));
        StatesTransitionSequence.RunResult result = sampler.runUntilCoalescence();
        System.out.println("Sampled state: " +  result.sampledState + "\t Steps: " + result.steps);
    }
}