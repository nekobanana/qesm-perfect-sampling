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
import org.qesm.app.model.generator.distribution.SingleValueDistribution;
import org.qesm.app.model.generator.distribution.UniformDistribution;
import org.qesm.app.model.sampling.runner.DumbSampleRunner;
import org.qesm.app.model.sampling.runner.ForwardCouplingRunner;
import org.qesm.app.model.sampling.runner.ForwardSampleRunner;
import org.qesm.app.model.sampling.runner.PerfectSampleRunner;
import org.qesm.app.model.sampling.sampler.DumbSampler;
import org.qesm.app.model.sampling.sampler.ForwardCoupler;
import org.qesm.app.model.sampling.sampler.ForwardSampler;
import org.qesm.app.model.sampling.sampler.PerfectSampler;
import org.qesm.app.model.sampling.sampler.random.SingleRandomHelper;
import org.qesm.app.model.test.StatisticalTest;
import org.qesm.app.model.test.ZTest;
import org.qesm.app.model.utils.Metrics;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;
import org.oristool.models.gspn.chains.DTMCStationary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.math.Quantiles.percentiles;

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

    public static Map<Integer, Matrix> transientAnalysis(Matrix P, Map<Integer, Double> steadyStateDistribution, double error) {
        assert(P.rows() == P.columns());
        int n = P.rows();
        double[] pi_0_arr = new double[n];
        Arrays.fill(pi_0_arr, (double) 1 / n);
        Matrix pi_0 = Matrix.from1DArray(1, n, pi_0_arr);
        double[] ssDist_arr = new double[n];
        for (int i = 0; i < n; i++) {
            ssDist_arr[i] = steadyStateDistribution.getOrDefault(i, 0.);
        }
        Matrix ssDist = Matrix.from1DArray(1, n, ssDist_arr);
        Matrix pi_n = pi_0.copy();
        Map<Integer, Matrix> results = new HashMap<>();
        int t = 0;
        results.put(t, pi_n);
        while (ssDist.subtract(pi_n).norm() > error * n) {
            t++;
            pi_n = pi_n.multiply(P);
            results.put(t, pi_n.toDenseMatrix());
        }
        return results;
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
            int N = 4;
            boolean connectSCCs = false;
            Distribution edgesNumberDistribution = new SingleValueDistribution( 4);
            Distribution edgesLocalityDistribution = new UniformDistribution(-2, 2);
            Double selfLoopValue = null;
            String description = "";
            Class<ZTest> testClass = ZTest.class;
            double testConfidence = 0.95;
            double testMaxError = 0.001;
            boolean outputHistogram = true;
            boolean outputSeqDiagram = false;
            Class<SingleRandomHelper> randomHelperClass = SingleRandomHelper.class;

            Config config = new Config();
            config.setDescription(description);
            config.getDtmcGeneratorConfig().setN(N);
            config.getDtmcGeneratorConfig().setConnectSCCs(connectSCCs);
            config.getDtmcGeneratorConfig().setEdgesNumberDistribution(edgesNumberDistribution);
            config.getDtmcGeneratorConfig().setEdgesLocalityDistribution(edgesLocalityDistribution);
            config.getDtmcGeneratorConfig().setSelfLoopValue(selfLoopValue);
            config.getPerfectSamplingConfig().setEnabled(true);
            config.getPerfectSamplingConfig().getStatisticalTestConfig().setTestClass(testClass);
            config.getPerfectSamplingConfig().getStatisticalTestConfig().setConfidence(testConfidence);
            config.getPerfectSamplingConfig().getStatisticalTestConfig().setError(testMaxError);
            config.getPerfectSamplingConfig().setRandomHelperClass(randomHelperClass);
            config.getPerfectSamplingConfig().setPythonHistogramImage(outputHistogram);
            config.getPerfectSamplingConfig().setPythonLastSequenceImage(outputSeqDiagram);
            config.getTransientAnalysisConfig().setEnabled(true);
            config.getTransientAnalysisConfig().setMaxDistanceToSteadyState(0.0001);
            config.getDumbSamplingConfig().getDumbSamplingPSConfig().setEnabled(true);
            config.getDumbSamplingConfig().getDumbSamplingPSConfig().setPerfectSamplingSigmas(new double[]{-2, -1, 0, 1, 2});
            config.setForwardSamplingConfig(new Config.ForwardSamplingConfig());
            config.getForwardSamplingConfig().setEnabled(true);
            config.getForwardSamplingConfig().setRandomHelperClass(randomHelperClass);
            config.setForwardCouplingConfig(new Config.ForwardCouplingConfig());
            config.getForwardCouplingConfig().setEnabled(true);
            writeFile(config, configOutputFile);
        }
        else { // load config file and start experiment
            Config config = new ObjectMapper().readValue(new File(inputFilePath), Config.class);
            Output output = null;
            try {
                output = runExperiment(config, new File(outputFilePath).getName());
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            writeFile(output, outputFilePath);
        }
    }

    public static Output runExperiment(Config configuration, String outputFileName) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {

        long seed;
        if (configuration.getSeed() != null) {
            seed = configuration.getSeed();
        }
        else {
            seed = new Random().nextInt();
            configuration.setSeed(seed);
        }

        int N = configuration.getDtmcGeneratorConfig().getN();

        System.out.println("***** " + outputFileName + " *****");
        System.out.println("Seed: " + seed);
        RandomUtils.rand.setSeed(seed);

        Output output = new Output();
        output.setConfig(configuration);
        output.setFileName(outputFileName);

        System.out.println("Generating matrix...");
        DTMCGenerator dtmcGenerator = new DTMCGenerator();
        dtmcGenerator.setEdgesNumberDistribution(configuration.getDtmcGeneratorConfig().getEdgesNumberDistribution());
        dtmcGenerator.setEdgesLocalityDistribution(configuration.getDtmcGeneratorConfig().getEdgesLocalityDistribution());
        dtmcGenerator.setN(N);
        Matrix P = dtmcGenerator.getMatrix();
        output.setDtmcGeneratorOutput(new Output.DTMCGeneratorOutput());
        output.getDtmcGeneratorOutput().setP(P);

        // Steady state distribution
        System.out.println("\nSteady state distribution...");
        Map<Integer, Double> solutionSS = getSteadyStateDistributionOris(P);
//        System.out.println(solutionSS);
        System.out.println("Done");
        output.setSteadyStateAnalysisOutput(new Output.SteadyStateAnalysisOutput());
        output.getSteadyStateAnalysisOutput().setSteadyStateDistribution(solutionSS);

        // Transient analysis
        if (configuration.getTransientAnalysisConfig() != null && configuration.getTransientAnalysisConfig().isEnabled()) {
            System.out.println("\nTransient analysis...");
            double errorStopCondition = configuration.getTransientAnalysisConfig().getMaxDistanceToSteadyState();
            Map<Integer, Matrix> transientResults = transientAnalysis(P, solutionSS, errorStopCondition);
            System.out.println("Done");
            Map<String, Object> transientJson = new HashMap<>();
            transientJson.put("steadyStateDistribution", solutionSS);
            transientJson.put("transientAnalysis", transientResults);
            String outputDirName = outputFileName.split("[.]")[0];
            Files.createDirectories(Paths.get("postprocess/results/" + outputDirName));
            writeFile(transientJson, "postprocess/results/" + outputDirName + "/transient.json");
        }
        String dirName = FilenameUtils.removeExtension(outputFileName);
        Output.PerfectSamplingOutput psOutput = null;

        // Perfect Sampling
        if (configuration.getPerfectSamplingConfig() != null && configuration.getPerfectSamplingConfig().isEnabled()) {
            System.out.println("\nPerfect sampling: ");
            Config.PerfectSamplingConfig psConfig = configuration.getPerfectSamplingConfig();
            PerfectSampler samplerCFTP = new PerfectSampler(P, psConfig.getRandomHelperClass(), psConfig.isPythonLastSequenceImage());
            PerfectSampleRunner perfectSampleRunner = new PerfectSampleRunner(samplerCFTP);
            StatisticalTest statTest = psConfig.getStatisticalTestConfig().getTestClass().getDeclaredConstructor().newInstance();
            statTest.setConfidence(psConfig.getStatisticalTestConfig().getConfidence());
            statTest.setMaxError(psConfig.getStatisticalTestConfig().getError());
            perfectSampleRunner.run(statTest);
            System.out.println("Runs: " + statTest.getSamplesSize() + "\t" + statTest.toString());
            Map<Integer, Double> piCFTP = perfectSampleRunner.getStatesDistribution(false);
            System.out.println("Distance / N: " + Metrics.distanceL2DividedByN(solutionSS, piCFTP, N));
            System.out.println("Total variation distance: " + Metrics.totalVariationDistance(solutionSS, piCFTP, N));
            try {
                if (psConfig.isPythonHistogramImage()) {
                    perfectSampleRunner.writeResultsOutput(dirName);
                }
                if (psConfig.isPythonLastSequenceImage()) {
                    perfectSampleRunner.writeSequenceOutput(dirName);
                }
            } catch (IOException e) {
                System.out.println("Cannot write Perfect sampling output file");
            }
            psOutput = new Output.PerfectSamplingOutput();
            psOutput.setStatisticalTest(statTest);
            psOutput.setAvgSteps(perfectSampleRunner.getAvgSteps());
            psOutput.setSigma(perfectSampleRunner.getStdDevSteps());
            psOutput.setL2DividedByNDistance(Metrics.distanceL2DividedByN(solutionSS, piCFTP, N));
            psOutput.setTotalVariationDistance(Metrics.totalVariationDistance(solutionSS, piCFTP, N));
            output.setPerfectSamplingOutput(psOutput);
        }

        //Forward Sampling
        if (configuration.getForwardSamplingConfig() != null && configuration.getForwardSamplingConfig().isEnabled()
                && psOutput != null) {
            ForwardSampler forwardSampler = new ForwardSampler(P, configuration.getForwardSamplingConfig().getRandomHelperClass());
            ForwardSampleRunner forwardCouplingRunner = (new ForwardSampleRunner(forwardSampler));
            System.out.println("\nForward sampling");
            forwardCouplingRunner.run(psOutput.getStatisticalTest().getSamplesSize());
            System.out.println("Done");
            Output.ForwardSamplingOutput fcOutput = new Output.ForwardSamplingOutput();
            fcOutput.setAvgSteps(forwardCouplingRunner.getAvgSteps());
            fcOutput.setSigma(forwardCouplingRunner.getStdDevSteps());
            output.setForwardSamplingOutput(fcOutput);
            forwardCouplingRunner.writeResultsOutput(dirName);
        }
        if (psOutput == null) {
            psOutput = configuration.getPreviousPerfectSamplingOutput();
        }

        Output.ForwardCouplingOutput fcOutput = null;
        //Forward Coupling
        Config.ForwardCouplingConfig forwardCouplingConfig = configuration.getForwardCouplingConfig();
        if (forwardCouplingConfig != null && forwardCouplingConfig.isEnabled()
//                && (psOutput != null || !forwardCouplingConfig.isUsePerfectSamplingSampleSize())
        ) {
            ForwardCoupler forwardCoupler = new ForwardCoupler(P);
            ForwardCouplingRunner forwardCouplingRunner = (new ForwardCouplingRunner(forwardCoupler));
//            int sampleSize = forwardCouplingConfig.isUsePerfectSamplingSampleSize()? psOutput.getStatisticalTest().getSamplesSize():
//                    forwardCouplingConfig.getSampleSize();
            forwardCouplingRunner.run(0);
            System.out.println("\nForward coupling");
            fcOutput = new Output.ForwardCouplingOutput();
            System.out.println("Done");
            fcOutput.setAvgSteps(forwardCouplingRunner.getAvgSteps());
            fcOutput.setSigma(forwardCouplingRunner.getStdDevSteps());
            int[] numbers = new int[101];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = i;
            }
            Map<Integer, Double> percentiles = percentiles().indexes(numbers).compute(forwardCouplingRunner.getResultsSteps());
            fcOutput.setQuantiles(percentiles.values().toArray(new Double[0]));
            output.setForwardCouplingOutput(fcOutput);
            forwardCouplingRunner.writeResultsOutput(dirName);
        }

        //Dumb Sampling
        Config.DumbSamplingConfig dsConfig = configuration.getDumbSamplingConfig();
        if (dsConfig != null && dsConfig.getDumbSamplingPSConfig() != null) {
            if (dsConfig.getDumbSamplingPSConfig().isEnabled()) {
                DumbSampler dumbSampler = new DumbSampler(P);
                int samplesNumber = psOutput.getStatisticalTest().getSamplesSize();
                for (double sigma : dsConfig.getDumbSamplingPSConfig().getPerfectSamplingSigmas()) {
                    int nSteps = getAvgStepsPlusStdDev(psOutput.getAvgSteps(), psOutput.getSigma(), sigma);
                    DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler))
                            .steps(nSteps);
                    System.out.println("\nDumb sampling (Perfect Sampling mean + " + sigma + " sigma): ");
                    dumbSampleRunner.run(samplesNumber);
                    Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(false);
                    System.out.println("Distance / N: " + Metrics.distanceL2DividedByN(solutionSS, piDumb, N));
                    System.out.println("Total variation distance: " + Metrics.totalVariationDistance(solutionSS, piDumb, N));
                    Output.DumbSamplingOutputPS dsOutput = new Output.DumbSamplingOutputPS();
                    dsOutput.setSteps(nSteps);
                    dsOutput.setSigma(sigma);
                    dsOutput.setDescription("Perfect Sampling");
                    dsOutput.setL2DividedByNDistance(Metrics.distanceL2DividedByN(solutionSS, piDumb, N));
                    dsOutput.setTotalVariationDistance(Metrics.totalVariationDistance(solutionSS, piDumb, N));
                    output.getDumbSamplingOutputsPS().add(dsOutput);
                }
            }
            if (dsConfig.getDumbSamplingFCConfig().isEnabled()) {
                if (fcOutput == null) {
                    fcOutput = configuration.getPreviousForwardCouplingOutput();
                }
                DumbSampler dumbSampler = new DumbSampler(P);
                for (int percentile : dsConfig.getDumbSamplingFCConfig().getForwardCouplingPercentiles()) {
                    int nSteps = fcOutput.getQuantiles()[percentile].intValue();
                    DumbSampleRunner dumbSampleRunner = (new DumbSampleRunner(dumbSampler)).steps(nSteps);
                    System.out.println("\nDumb sampling (Forward Coupling " + percentile + " percentile): ");
                    dumbSampleRunner.run(psOutput.getStatisticalTest().getSamplesSize());
                    Map<Integer, Double> piDumb = dumbSampleRunner.getStatesDistribution(false);
                    System.out.println("Distance / N: " + Metrics.distanceL2DividedByN(solutionSS, piDumb, N));
                    System.out.println("Total variation distance: " + Metrics.totalVariationDistance(solutionSS, piDumb, N));
                    Output.DumbSamplingOutputFC dsOutput = new Output.DumbSamplingOutputFC();
                    dsOutput.setDescription("Forward Coupling");
                    dsOutput.setSteps(nSteps);
                    dsOutput.setQuantile(percentile);
                    dsOutput.setL2DividedByNDistance(Metrics.distanceL2DividedByN(solutionSS, piDumb, N));
                    dsOutput.setTotalVariationDistance(Metrics.totalVariationDistance(solutionSS, piDumb, N));
                    output.getDumbSamplingOutputsFC().add(dsOutput);
                }
            }
        }

        System.out.println("\n\n");
        return output;
    }

    public static void writeFile(Object object, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
        writer.close();
    }

    public static int getAvgStepsPlusStdDev(double avg, double stdDev, double sigmaCount) {
        return (int) Math.round(avg + sigmaCount * stdDev);
    }
}
