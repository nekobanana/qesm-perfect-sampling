package org.example;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.generator.DTMCGenerator;
import org.example.model.generator.Distribution;
import org.example.model.generator.UniformDistribution;
import org.example.model.sampling.runner.DumbSampleRunner;
import org.example.model.sampling.runner.PerfectSampleRunner;
import org.example.model.sampling.sampler.DumbSampler;
import org.example.model.sampling.sampler.PerfectSampler;
import org.example.model.utils.Metrics;
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
        int N = 16;
        Distribution edgesNumberDistribution = new UniformDistribution(1, (int)Math.sqrt(N));
        Distribution edgesLocalityDistribution = new UniformDistribution(-(int)Math.sqrt(N), (int)Math.sqrt(N));
        double selfLoopProbability = 0.;
//        long seed = 1;
        long seed = new Random().nextInt();
        System.out.println("Seed: " + seed);

        DTMCGenerator dtmcGenerator = new DTMCGenerator(seed, N, edgesNumberDistribution, edgesLocalityDistribution, selfLoopProbability);
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

        final int runs = 100000;
        System.out.println("Runs: " + runs);
        PerfectSampler samplerCFTP = new PerfectSampler(P);
        PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
        perfectSampleRunner.run(runs);
        Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(true);
        System.out.println("Distance: " + Metrics.distanceL2(solutionSS, piCFTP));
//        perfectSampleRunner.getStepsDistribution(true);
        try {
            perfectSampleRunner.writeOutputs();
        } catch (IOException e) {
        }

        DumbSampler dumbSampler = new DumbSampler(P);
        for (int sigma = 0; sigma <= 3; sigma++) {
            DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
                    .steps(perfectSampleRunner.getAvgStepsPlusStdDev(sigma));
            dumbSampleRunner.run(runs);
            Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(true);
            System.out.println("Distance: " + Metrics.distanceL2(solutionSS, piDumb));
        }
    }



}