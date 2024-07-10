package org.qesm.app.model.test;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.distribution.NormalDistribution;

public abstract class StatisticalTest {
    protected String name;
    protected double confidence;
    protected double maxError;
    protected int samplesSize;
    private double samplesAvg;
    private double samplesStdDev;
    private double currentError;

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

    protected double getCriticalValue() {
        throw new NotImplementedException();
    }

    public void updateStats(int samplesSize, double avg, double stdDev) {
        this.samplesSize = samplesSize;
        this.samplesAvg = avg;
        this.samplesStdDev = stdDev;
    }

    public boolean test() {
        double standardError = samplesStdDev / Math.sqrt(samplesSize);
        double value = getCriticalValue();
        double marginOfError = value * standardError;
        currentError = marginOfError / samplesAvg;
        return currentError <= maxError;
    }

    @Override
    public String toString() {
        return name + "{" +
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
