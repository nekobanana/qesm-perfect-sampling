package org.qesm.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.apache.commons.cli.*;
import org.qesm.app.model.Config;
import org.qesm.app.model.Output;
import org.qesm.app.model.generator.DTMCGenerator;
import org.qesm.app.model.generator.distribution.Distribution;
import org.qesm.app.model.generator.distribution.ManualDistribution;
import org.qesm.app.model.generator.distribution.SingleValueDistribution;
import org.qesm.app.model.generator.distribution.UniformDistribution;
import org.qesm.app.model.sampling.runner.DumbSampleRunner;
import org.qesm.app.model.sampling.runner.PerfectSampleRunner;
import org.qesm.app.model.sampling.sampler.DumbSampler;
import org.qesm.app.model.sampling.sampler.PerfectSampler;
import org.qesm.app.model.utils.Metrics;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static Map<Integer, Double> getSteadyStateDistributionOris(Matrix P) {
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

        Option postProcess = new Option("post", "post", false, "generate output for post process");
        postProcess.setRequired(false);
        postProcess.setType(Boolean.class);
        options.addOption(postProcess);

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
            Boolean genPostProcessOutput = cmd.getParsedOptionValue("post");
            if (genPostProcessOutput == null) genPostProcessOutput = false;
            configExperiment(inputFilePath, outputFilePath, configFilePath, genPostProcessOutput);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("perfect-sampling", options);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void configExperiment(String inputFilePath, String outputFilePath, String configOutputFile,
                                        boolean genPostProcessOutput) throws IOException {
        if (configOutputFile != null) { // Only to generate configuration file, no experiment
            int N = 16;
            int runs = 1600;
            boolean connectSCCs = false;
            Distribution edgesNumberDistribution = new SingleValueDistribution( 4);
            Distribution edgesLocalityDistribution = ManualDistribution.ManualDistributionBuilder.newBuilder()
                    .distribution(new UniformDistribution(-2, -1), 1)
                    .distribution(new SingleValueDistribution(0), 2)
                    .distribution(new UniformDistribution(1, 2), 1)
                    .build();
//            Distribution edgesLocalityDistribution = new UniformDistribution(-2, 2);
            double selfLoopValue = 0.4;

            Config config = new Config();
            config.setN(N);
            config.setRun(runs);
            config.setConnectSCCs(connectSCCs);
            config.setEdgesNumberDistribution(edgesNumberDistribution);
            config.setEdgesLocalityDistribution(edgesLocalityDistribution);
            config.setSelfLoopValue(selfLoopValue);
            writeFile(config, configOutputFile);
        }
        else { // load config file and start experiment
            Config config = new ObjectMapper().readValue(new File(inputFilePath), Config.class);
            Output output = runExperiment(config, genPostProcessOutput);
            writeFile(output, outputFilePath);
        }
    }

    public static Output runExperiment(Config configuration, boolean genPostProcessOutput) {

        int N = configuration.getN();
        Distribution edgesNumberDistribution = configuration.getEdgesNumberDistribution();
        Distribution edgesLocalityDistribution = configuration.getEdgesLocalityDistribution();

        double selfLoopValue = configuration.getSelfLoopValue();
        long seed;
        if (configuration.getSeed() != null) {
            seed = configuration.getSeed();
        }
        else {
            seed = new Random().nextInt();
            configuration.setSeed(seed);
        }

        System.out.println("Seed: " + seed);
        RandomUtils.rand.setSeed(seed);
        System.out.println("Generating matrix...");
        DTMCGenerator dtmcGenerator = new DTMCGenerator(N, edgesNumberDistribution,
                edgesLocalityDistribution, selfLoopValue, configuration.isConnectSCCs());
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionOris(P);
        System.out.println(solutionSS);

        Output output = new Output();
        output.setConfig(configuration);

        // Perfect Sampling

        final int runs = configuration.getRun();
        System.out.println("Runs: " + runs);
        PerfectSampler samplerCFTP = new PerfectSampler(P);
        PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
        perfectSampleRunner.run(runs);
        Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(false);
//        System.out.println("\nPerfect sampling: ");
//        System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piCFTP, N));
//        perfectSampleRunner.getStepsDistribution(true);
        if (genPostProcessOutput) {
            try {
                perfectSampleRunner.writeOutputs();
            } catch (IOException e) {
                System.out.println("Cannot write Perfect sampling output file");
            }
        }
        Output.PerfectSamplingOutput psOutput = new Output.PerfectSamplingOutput();
        psOutput.setAvgSteps(perfectSampleRunner.getAvgSteps());
        psOutput.setSigma(perfectSampleRunner.getStdDevSteps());
        psOutput.setDistance(Metrics.distanceL2PerN(solutionSS, piCFTP, N));
        output.setPerfectSamplingOutput(psOutput);

        //Dumb Sampling

        DumbSampler dumbSampler = new DumbSampler(P);
        for (int sigma = 0; sigma <= 3; sigma++) {
            int nSteps = perfectSampleRunner.getAvgStepsPlusStdDev(sigma);
            DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
                    .steps(nSteps);
            dumbSampleRunner.run(runs);
            Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(false);
            System.out.println("\nDumb sampling (" + sigma + " sigma): ");
            System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piDumb, N));
            Output.DumbSamplingOutput dsOutput = new Output.DumbSamplingOutput();
            dsOutput.setSteps(nSteps);
            dsOutput.setSigmas(sigma);
            dsOutput.setDistance(Metrics.distanceL2PerN(solutionSS, piDumb, N));
            output.getDumbSamplingOutputs().add(dsOutput);
        }
        return output;
    }

    public static void writeFile(Object object, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(new ObjectMapper().writeValueAsString(object));
        writer.close();
    }
}