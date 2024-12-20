package org.qesm.app.model;

import org.qesm.app.model.generator.distribution.Distribution;
import org.qesm.app.model.sampling.sampler.random.RandomHelper;
import org.qesm.app.model.test.StatisticalTest;

public class Config {

    private Long seed;
    private String description;
    private DTMCGeneratorConfig dtmcGeneratorConfig = new DTMCGeneratorConfig();
    private PerfectSamplingConfig perfectSamplingConfig = new PerfectSamplingConfig();
    private ForwardSamplingConfig forwardSamplingConfig = new ForwardSamplingConfig();
    private DumbSamplingConfig dumbSamplingConfig = new DumbSamplingConfig();
    private ForwardCouplingConfig forwardCouplingConfig = new ForwardCouplingConfig();
    private TransientAnalysisConfig transientAnalysisConfig = new TransientAnalysisConfig();
    private Output.PerfectSamplingOutput previousPerfectSamplingOutput;
    private Output.ForwardCouplingOutput previousForwardCouplingOutput;

    public Output.ForwardCouplingOutput getPreviousForwardCouplingOutput() {
        return previousForwardCouplingOutput;
    }

    public void setPreviousForwardCouplingOutput(Output.ForwardCouplingOutput previousForwardCouplingOutput) {
        this.previousForwardCouplingOutput = previousForwardCouplingOutput;
    }

    public Output.PerfectSamplingOutput getPreviousPerfectSamplingOutput() {
        return previousPerfectSamplingOutput;
    }

    public void setPreviousPerfectSamplingOutput(Output.PerfectSamplingOutput previousPerfectSamplingOutput) {
        this.previousPerfectSamplingOutput = previousPerfectSamplingOutput;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public ForwardCouplingConfig getForwardCouplingConfig() {
        return forwardCouplingConfig;
    }

    public void setForwardCouplingConfig(ForwardCouplingConfig forwardCouplingConfig) {
        this.forwardCouplingConfig = forwardCouplingConfig;
    }

    public DumbSamplingConfig getDumbSamplingConfig() {
        return dumbSamplingConfig;
    }

    public void setDumbSamplingConfig(DumbSamplingConfig dumbSamplingConfig) {
        this.dumbSamplingConfig = dumbSamplingConfig;
    }

    public TransientAnalysisConfig getTransientAnalysisConfig() {
        return transientAnalysisConfig;
    }

    public void setTransientAnalysisConfig(TransientAnalysisConfig transientAnalysisConfig) {
        this.transientAnalysisConfig = transientAnalysisConfig;
    }

    public DTMCGeneratorConfig getDtmcGeneratorConfig() {
        return dtmcGeneratorConfig;
    }

    public void setDtmcGeneratorConfig(DTMCGeneratorConfig dtmcGeneratorConfig) {
        this.dtmcGeneratorConfig = dtmcGeneratorConfig;
    }

    public PerfectSamplingConfig getPerfectSamplingConfig() {
        return perfectSamplingConfig;
    }

    public void setPerfectSamplingConfig(PerfectSamplingConfig perfectSamplingConfig) {
        this.perfectSamplingConfig = perfectSamplingConfig;
    }

    public ForwardSamplingConfig getForwardSamplingConfig() {
        return forwardSamplingConfig;
    }

    public void setForwardSamplingConfig(ForwardSamplingConfig forwardSamplingConfig) {
        this.forwardSamplingConfig = forwardSamplingConfig;
    }

    public static class DTMCGeneratorConfig {
        private int N;
        private Distribution edgesNumberDistribution;
        private Distribution edgesLocalityDistribution;
        private Double selfLoopValue;
        private boolean connectSCCs;

        public int getN() {
            return N;
        }

        public void setN(int n) {
            N = n;
        }

        public Distribution getEdgesNumberDistribution() {
            return edgesNumberDistribution;
        }

        public void setEdgesNumberDistribution(Distribution edgesNumberDistribution) {
            this.edgesNumberDistribution = edgesNumberDistribution;
        }

        public Distribution getEdgesLocalityDistribution() {
            return edgesLocalityDistribution;
        }

        public void setEdgesLocalityDistribution(Distribution edgesLocalityDistribution) {
            this.edgesLocalityDistribution = edgesLocalityDistribution;
        }

        public Double getSelfLoopValue() {
            return selfLoopValue;
        }

        public void setSelfLoopValue(Double selfLoopValue) {
            this.selfLoopValue = selfLoopValue;
        }

        public boolean isConnectSCCs() {
            return connectSCCs;
        }
        public void setConnectSCCs(boolean connectSCCs) {
            this.connectSCCs = connectSCCs;
        }
    }

    public static class PerfectSamplingConfig {
        private StatisticalTestConfig statisticalTestConfig = new StatisticalTestConfig();
        private Class<? extends RandomHelper> randomHelperClass;
        private boolean pythonHistogramImage;
        private boolean pythonLastSequenceImage;
        private boolean enabled = false;

        public boolean isPythonHistogramImage() {
            return pythonHistogramImage;
        }

        public void setPythonHistogramImage(boolean pythonHistogramImage) {
            this.pythonHistogramImage = pythonHistogramImage;
        }

        public boolean isPythonLastSequenceImage() {
            return pythonLastSequenceImage;
        }

        public void setPythonLastSequenceImage(boolean pythonLastSequenceImage) {
            this.pythonLastSequenceImage = pythonLastSequenceImage;
        }

        public StatisticalTestConfig getStatisticalTestConfig() {
            return statisticalTestConfig;
        }

        public void setStatisticalTestConfig(StatisticalTestConfig statisticalTestConfig) {
            this.statisticalTestConfig = statisticalTestConfig;
        }

        public Class<? extends RandomHelper> getRandomHelperClass() {
            return randomHelperClass;
        }

        public void setRandomHelperClass(Class<? extends RandomHelper> randomHelperClass) {
            this.randomHelperClass = randomHelperClass;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class StatisticalTestConfig {
        private double confidence;
        private double error;
        private Class<? extends StatisticalTest> testClass;

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public double getError() {
            return error;
        }

        public void setError(double error) {
            this.error = error;
        }

        public Class<? extends StatisticalTest> getTestClass() {
            return testClass;
        }

        public void setTestClass(Class<? extends StatisticalTest> testClass) {
            this.testClass = testClass;
        }
    }
    public static class DumbSamplingConfig {
        private DumbSamplingPSConfig dumbSamplingPSConfig = new DumbSamplingPSConfig();
        private DumbSamplingFCConfig dumbSamplingFCConfig = new DumbSamplingFCConfig();

        public DumbSamplingPSConfig getDumbSamplingPSConfig() {
            return dumbSamplingPSConfig;
        }

        public void setDumbSamplingPSConfig(DumbSamplingPSConfig dumbSamplingPSConfig) {
            this.dumbSamplingPSConfig = dumbSamplingPSConfig;
        }

        public DumbSamplingFCConfig getDumbSamplingFCConfig() {
            return dumbSamplingFCConfig;
        }

        public void setDumbSamplingFCConfig(DumbSamplingFCConfig dumbSamplingFCConfig) {
            this.dumbSamplingFCConfig = dumbSamplingFCConfig;
        }
    }
    public static class DumbSamplingPSConfig {
        private boolean enabled = false;
        private double[] perfectSamplingSigmas;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public double[] getPerfectSamplingSigmas() {
            return perfectSamplingSigmas;
        }

        public void setPerfectSamplingSigmas(double[] perfectSamplingSigmas) {
            this.perfectSamplingSigmas = perfectSamplingSigmas;
        }
    }

        public static class DumbSamplingFCConfig {
        private boolean enabled = false;
        private int[] forwardCouplingPercentiles;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int[] getForwardCouplingPercentiles() {
            return forwardCouplingPercentiles;
        }

        public void setForwardCouplingPercentiles(int[] forwardCouplingPercentiles) {
            this.forwardCouplingPercentiles = forwardCouplingPercentiles;
        }

    }

    public static class TransientAnalysisConfig {
        private boolean enabled = false;
        private double maxDistanceToSteadyState;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public double getMaxDistanceToSteadyState() {
            return maxDistanceToSteadyState;
        }

        public void setMaxDistanceToSteadyState(double maxDistanceToSteadyState) {
            this.maxDistanceToSteadyState = maxDistanceToSteadyState;
        }
    }

    public static class ForwardSamplingConfig {
        private boolean enabled = false;
        private Class<? extends RandomHelper> randomHelperClass;

        public Class<? extends RandomHelper> getRandomHelperClass() {
            return randomHelperClass;
        }

        public void setRandomHelperClass(Class<? extends RandomHelper> randomHelperClass) {
            this.randomHelperClass = randomHelperClass;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ForwardCouplingConfig {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }
}
