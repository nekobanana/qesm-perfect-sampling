package org.qesm.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
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
import org.qesm.app.model.test.StatisticalTest;
import org.qesm.app.model.test.StudentTTest;
import org.qesm.app.model.test.ZTest;
import org.qesm.app.model.utils.Metrics;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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
//
//        Option postProcess = new Option("post", "post", false, "generate output for post process");
//        postProcess.setRequired(false);
//        postProcess.setType(Boolean.class);
//        options.addOption(postProcess);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

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
//            Boolean genPostProcessOutput = cmd.getParsedOptionValue("post");
            configExperiment(inputFilePath, outputFilePath, configFilePath);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("perfect-sampling", options);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void configExperiment(String inputFilePath, String outputFilePath, String configOutputFile) throws IOException {
        if (configOutputFile != null) { // Only to generate configuration file, no experiment
            int N = 16;
//            int dtmcNumber = 10;
            boolean connectSCCs = false;
            Distribution edgesNumberDistribution = new SingleValueDistribution( 4);
            Distribution edgesLocalityDistribution = new UniformDistribution(-2, 2);
            Double selfLoopValue = null;
            String description = "";
            Class testClass = ZTest.class;
            double testConfidence = 0.95;
            double testMaxError = 0.001;
            boolean outputHistogram = true;
            boolean outputSeqDiagram = false;

            Config config = new Config();
            config.setN(N);
//            config.setDtmcNumber(dtmcNumber);
            config.setConnectSCCs(connectSCCs);
            config.setEdgesNumberDistribution(edgesNumberDistribution);
            config.setEdgesLocalityDistribution(edgesLocalityDistribution);
            config.setSelfLoopValue(selfLoopValue);
            config.setDescription(description);
            config.getStatisticalTestConfig().setTestClass(testClass);
            config.getStatisticalTestConfig().setConfidence(testConfidence);
            config.getStatisticalTestConfig().setError(testMaxError);
            config.setPythonHistogramImage(outputHistogram);
            config.setPythonLastSequenceImage(outputSeqDiagram);
            writeFile(config, configOutputFile);
        }
        else { // load config file and start experiment
            Config config = new ObjectMapper().readValue(new File(inputFilePath), Config.class);
            Output output = null;
            try {
                output = runExperiment(config, new File(outputFilePath).getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            writeFile(output, outputFilePath);
        }
    }

    public static Output runExperiment(Config configuration, String outputFileName) throws IllegalAccessException, InstantiationException {

        long seed;
        if (configuration.getSeed() != null) {
            seed = configuration.getSeed();
        }
        else {
            seed = new Random().nextInt();
            configuration.setSeed(seed);
        }

        int N = configuration.getN();

        System.out.println("Seed: " + seed);
        RandomUtils.rand.setSeed(seed);

        System.out.println("Generating matrix...");
        DTMCGenerator dtmcGenerator = new DTMCGenerator();
        dtmcGenerator.setEdgesNumberDistribution(configuration.getEdgesNumberDistribution());
        dtmcGenerator.setEdgesLocalityDistribution(configuration.getEdgesLocalityDistribution());
        dtmcGenerator.setN(N);
        Matrix P = dtmcGenerator.getMatrix();

        // Steady state distribution

        Map<Integer, Double> solutionSS = getSteadyStateDistributionOris(P);
        System.out.println(solutionSS);

        Output output = new Output();
        output.setConfig(configuration);
        output.setFileName(outputFileName);
        // Perfect Sampling

        System.out.println("Running...");
        PerfectSampler samplerCFTP = new PerfectSampler(P);
        PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
        StatisticalTest statTest = (StatisticalTest) configuration.getStatisticalTestConfig().getTestClass().newInstance();
        statTest.setConfidence(configuration.getStatisticalTestConfig().getConfidence());
        statTest.setMaxError(configuration.getStatisticalTestConfig().getError());
        perfectSampleRunner.run(statTest);
        System.out.println("Runs: " + statTest.getSamplesSize() + "\t" + statTest.toString());
        Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(false);
        System.out.println("\nPerfect sampling: ");
        System.out.println("Distance / N: " + Metrics.distanceL2PerN(solutionSS, piCFTP, N));
        perfectSampleRunner.getStepsDistribution(false);
        try {
            String dirName = FilenameUtils.removeExtension(outputFileName);
            if (configuration.isPythonHistogramImage()) {
                perfectSampleRunner.writeResultsOutput(dirName);
            }
            if (configuration.isPythonLastSequenceImage()) {
                perfectSampleRunner.writeSequenceOutput(dirName);
            }
            perfectSampleRunner.waitForOutputWrite();
        } catch (IOException e) {
            System.out.println("Cannot write Perfect sampling output file");
        }
        Output.PerfectSamplingOutput psOutput = new Output.PerfectSamplingOutput();
        psOutput.setAvgSteps(perfectSampleRunner.getAvgSteps());
        psOutput.setSigma(perfectSampleRunner.getStdDevSteps());
        psOutput.setDistance(Metrics.distanceL2PerN(solutionSS, piCFTP, N));
        output.setPerfectSamplingOutput(psOutput);

        //Dumb Sampling

        DumbSampler dumbSampler = new DumbSampler(P);
        for (int sigma = 0; sigma <= 2; sigma++) {
            int nSteps = perfectSampleRunner.getAvgStepsPlusStdDev(sigma);
            DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
                    .steps(nSteps);
            dumbSampleRunner.run(statTest.getSamplesSize());
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