package org.qesm.app.model;

import org.qesm.app.model.generator.distribution.Distribution;
import org.qesm.app.model.sampling.sampler.random.RandomHelper;
import org.qesm.app.model.test.StatisticalTest;

public class Config {
    private int N;
    private Distribution edgesNumberDistribution;
    private Distribution edgesLocalityDistribution;
    private Double selfLoopValue;
    private Long seed;
    private StatisticalTestConfig statisticalTestConfig = new StatisticalTestConfig();
    private Class<? extends RandomHelper> randomHelperClass;
    private boolean connectSCCs;
    private String description;

    private boolean pythonHistogramImage;
    private boolean pythonLastSequenceImage;

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

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

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public boolean isConnectSCCs() {
        return connectSCCs;
    }
    public void setConnectSCCs(boolean connectSCCs) {
        this.connectSCCs = connectSCCs;
    }

    public String getDescription() {
        return description;
    }

    public StatisticalTestConfig getStatisticalTestConfig() {
        return statisticalTestConfig;
    }

    public void setStatisticalTestConfig(StatisticalTestConfig statisticalTestConfig) {
        this.statisticalTestConfig = statisticalTestConfig;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public Class<? extends RandomHelper> getRandomHelperClass() {
        return randomHelperClass;
    }

    public void setRandomHelperClass(Class<? extends RandomHelper> randomHelperClass) {
        this.randomHelperClass = randomHelperClass;
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
}
