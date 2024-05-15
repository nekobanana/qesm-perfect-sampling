package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.apache.commons.cli.*;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        Option config = new Option("c", "config", true, "generated configuration file");
        config.setRequired(false);
        options.addOption(config);

        Option seed = new Option("s", "seed", true, "seed for random");
        seed.setRequired(false);
        seed.setType(Long.class);
        options.addOption(seed);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
//            if (!((cmd.hasOption("i") && cmd.hasOption("o")) ^ cmd.hasOption("c"))) {
            if ((cmd.hasOption("i") && cmd.hasOption("o")) == cmd.hasOption("c")) {
                throw new ParseException("You must specify either both --input and --output options" +
                        " or --config option only");
            }
            String inputFilePath = cmd.getOptionValue("input");
            String outputFilePath = cmd.getOptionValue("output");
            String configFilePath = cmd.getOptionValue("config");
            Long seedValue = cmd.getParsedOptionValue("seed");
            configExperiment(inputFilePath, outputFilePath, configFilePath, seedValue);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("perfect-sampling", options);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void configExperiment(String inputFilePath, String outputFilePath, String configOutputFile, Long seedValue) throws IOException {
        if (configOutputFile != null) { // Only to generate configuration file, no experiment
            long seed = new Random().nextInt();
//            long seed = 1;
            int N = 16;
            Distribution edgesNumberDistribution = new UniformDistribution(4, 4);
            Distribution edgesLocalityDistribution = ManualDistribution.ManualDistributionBuilder.newBuilder()
                    .distribution(new UniformDistribution(-2, -1), 1)
                    .distribution(new SingleValueDistribution(0), 2)
                    .distribution(new UniformDistribution(1, 2), 1)
                    .build();
//            Distribution edgesLocalityDistribution = new UniformDistribution(-2, 2);
            double selfLoopValue = 0.4;

            Config config = new Config();
            config.setN(N);
            config.setEdgesNumberDistribution(edgesNumberDistribution);
            config.setEdgesLocalityDistribution(edgesLocalityDistribution);
            config.setSelfLoopValue(selfLoopValue);
            config.setSeed(seed);
            BufferedWriter writer = new BufferedWriter(new FileWriter(configOutputFile));
            writer.write(new ObjectMapper().writeValueAsString(config));
            writer.close();
        }
        else { // load config file and start experiment
            Config config = new ObjectMapper().readValue(new File(inputFilePath), Config.class);
            runExperiment(config);
        }
    }

    public static void runExperiment(Config configuration) {

        int N = configuration.getN();
        Distribution edgesNumberDistribution = configuration.getEdgesNumberDistribution();
        Distribution edgesLocalityDistribution = configuration.getEdgesLocalityDistribution();

        double selfLoopValue = configuration.getSelfLoopValue();
        long seed = configuration.getSeed();
        System.out.println("Seed: " + seed);
        RandomUtils.rand.setSeed(seed);
        System.out.println("Generating matrix...");
        DTMCGenerator dtmcGenerator = new DTMCGenerator(N, edgesNumberDistribution, edgesLocalityDistribution, selfLoopValue, true);
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionLinearSystem(P);
        System.out.println(solutionSS);

        final int runs = 1;
        System.out.println("Runs: " + runs);
        PerfectSampler samplerCFTP = new PerfectSampler(P);
        PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
        perfectSampleRunner.run(runs);
        Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(false);
        System.out.println("\nPerfect sampling: ");
        System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piCFTP, N));
//        perfectSampleRunner.getStepsDistribution(true);
        try {
            perfectSampleRunner.writeOutputs();
        } catch (IOException e) {
            System.out.println("Cannot write output file");
        }

        DumbSampler dumbSampler = new DumbSampler(P);
        for (int sigma = 0; sigma <= 3; sigma++) {
            DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
                    .steps(perfectSampleRunner.getAvgStepsPlusStdDev(sigma));
            dumbSampleRunner.run(runs);
            Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(false);
            System.out.println("\nDumb sampling (" + sigma + " sigma): ");
            System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piDumb, N));
        }
    }
}