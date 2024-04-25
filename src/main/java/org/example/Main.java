package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.model.generator.DTMCGenerator;
import org.example.model.generator.Distribution;
import org.example.model.generator.UniformDistribution;
import org.example.model.sampler.DumbSampler;
import org.example.model.sampler.PerfectSamplerCFTP;
import org.example.model.sampler.PerfectSamplerForward;
import org.example.model.sampler.RunResult;
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
        int N = 10;
        Distribution edgesNumberDistribution = new UniformDistribution(1, (int)Math.sqrt(N));
        Distribution edgesLocalityDistribution = new UniformDistribution(-(int)Math.sqrt(N), (int)Math.sqrt(N));
        double selfLoopProbability = 0.;
        long seed = 1;

        DTMCGenerator dtmcGenerator = new DTMCGenerator(seed, N, edgesNumberDistribution, edgesLocalityDistribution, selfLoopProbability);
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

        final int runs = 10000;
        List<RunResult> resultsCFTP = new ArrayList<>();
        PerfectSamplerCFTP samplerCFTP = new PerfectSamplerCFTP(P);
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

        DumbSampler dumbSampler = new DumbSampler(P);
        List<Integer> resultsD = new ArrayList<>();
        int resultD;
        int initialState = 0;
        int stepsD = (int)avgStepsCFTP + 1;
        for (int i = 0; i < runs; i++) {
            dumbSampler.reset();
            initialState = (initialState + 1) % P.rows();
            resultD = dumbSampler.runForNSteps(initialState, stepsD);
            resultsD.add(resultD);
        }
        System.out.println("\nDumb sampling");
        System.out.println("Running for " + stepsD + " steps");
        Map<Integer, Long> pi2 = getDistrFromResults(resultsD, Function.identity());
        pi2.forEach((state, count) -> System.out.println("state " + state + ": " + (double)count / runs));

    }

    private static <T> Map<Integer, Long> getDistrFromResults(List<T> results, Function<? super T, Integer>  function) {
        return results.stream().collect(Collectors.groupingBy(function, Collectors.counting()));
    }

}