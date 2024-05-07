package org.example;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.generator.DTMCGenerator;
import org.example.model.generator.distribution.Distribution;
import org.example.model.generator.distribution.ManualDistribution;
import org.example.model.generator.distribution.SingleValueDistribution;
import org.example.model.generator.distribution.UniformDistribution;
import org.example.model.sampling.runner.DumbSampleRunner;
import org.example.model.sampling.runner.PerfectSampleRunner;
import org.example.model.sampling.sampler.DumbSampler;
import org.example.model.sampling.sampler.PerfectSampler;
import org.example.model.utils.Log;
import org.example.model.utils.Metrics;
import org.example.model.utils.RandomUtils;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static Map<Integer, Double> getSteadyStateDistributionLinearSystem(Matrix P) {
        MutableValueGraph<Object, Double> graph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        assert(P.rows() == P.columns());
        int n = P.rows();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph.putEdgeValue(i, j, P.get(i, j));
            }
        }
        return DTMCStationary.builder().build().apply(graph)
                .entrySet().stream()
                .collect(Collectors.toMap(e -> (int)e.getKey(), Map.Entry::getValue));
    }


    public static void main(String[] args) {
        int N = 4;
        Distribution edgesNumberDistribution = new UniformDistribution(1, 2);
        Distribution edgesLocalityDistribution = ManualDistribution.ManualDistributionBuilder.newBuilder()
                .distribution(new UniformDistribution(-2, -1), 1)
                .distribution(new SingleValueDistribution(0), 5)
                .distribution(new UniformDistribution(1, 2), 1)
                .build();

        double selfLoopValue = 0.4;
        long seed = 1;
//        long seed = new Random().nextInt();
        System.out.println("Seed: " + seed);
        RandomUtils.rand.setSeed(seed);
//        Random random = RandomUtils.rand;
        System.out.println("Generating matrix...");
        DTMCGenerator dtmcGenerator = new DTMCGenerator(N, edgesNumberDistribution, edgesLocalityDistribution, selfLoopValue);
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

//        final int runs = 10000;
//        System.out.println("Runs: " + runs);
//        PerfectSampler samplerCFTP = new PerfectSampler(P, random.nextLong());
//        PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
//        perfectSampleRunner.run(runs);
//        Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(false);
//        System.out.println("\nPerfect sampling: ");
//        System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piCFTP, N));
////        perfectSampleRunner.getStepsDistribution(true);
//        try {
//            perfectSampleRunner.writeOutputs();
//        } catch (IOException e) {
//        }
//
//        DumbSampler dumbSampler = new DumbSampler(P, random.nextLong());
//        for (int sigma = 0; sigma <= 3; sigma++) {
//            DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
//                    .steps(perfectSampleRunner.getAvgStepsPlusStdDev(sigma));
//            dumbSampleRunner.run(runs);
//            Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(false);
//            System.out.println("\nDumb sampling (" + sigma + " sigma): ");
//            System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piDumb, N));
//        }
    }
}