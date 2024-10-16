package org.qesm.app.model;

import org.qesm.app.model.generator.distribution.Distribution;
import org.qesm.app.model.sampling.sampler.random.RandomHelper;
import org.qesm.app.model.test.StatisticalTest;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private Long seed;
    private String description;
    private DTMCGeneratorConfig dtmcGeneratorConfig;
    private PerfectSamplingConfig perfectSamplingConfig;
    private DumbSamplingConfig dumbSamplingConfig;
    private TransientAnalysisConfig transientAnalysisConfig;


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
        private boolean enabled;

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
        private boolean enabled;
        private double[] sigmas;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public double[] getSigmas() {
            return sigmas;
        }

        public void setSigmas(double[] sigmas) {
            this.sigmas = sigmas;
        }
    }

    public static class TransientAnalysisConfig {
        private boolean enabled;
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
}
