package org.qesm.app.model.test;

import org.apache.commons.math3.distribution.TDistribution;

public class StudentTTest implements StatisticalTest {
    private int samplesSize;
    private double samplesAvg;
    private double samplesStdDev;
    private double confidence;
    private double maxError;
    private double currentError;

    public StudentTTest(double confidence, double maxError) {
        this.confidence = confidence;
        this.maxError = maxError;
    }

    @Override
    public void updateStats(int samplesSize, double avg, double stdDev) {
        this.samplesSize = samplesSize;
        this.samplesAvg = avg;
        this.samplesStdDev = stdDev;
    };

    @Override
    public boolean test() {
        double standardError = samplesStdDev / Math.sqrt(samplesSize);
        double tValue = getCriticalTValue(samplesSize - 1, confidence);
        double marginOfError = tValue * standardError;
        currentError = marginOfError / samplesAvg;
        return currentError <= maxError;
    }

    private static double getCriticalTValue(int degreesOfFreedom, double confidenceLevel) {
        TDistribution tDistribution = new TDistribution(degreesOfFreedom);
        return tDistribution.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);
    }

    @Override
    public String toString() {
        return "StudentTTest{" +
                "samplesSize=" + samplesSize +
                ", samplesAvg=" + samplesAvg +
                ", samplesStdDev=" + samplesStdDev +
                ", confidence=" + confidence +
                ", maxError=" + maxError +
                ", currentError=" + currentError +
                '}';
    }

    public int getSamplesSize() {
        return samplesSize;
    }
}
