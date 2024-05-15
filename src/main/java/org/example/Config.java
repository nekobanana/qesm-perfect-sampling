package org.example;

import org.example.model.generator.distribution.Distribution;

public class Config {
    private int N;
    private Distribution edgesNumberDistribution;
    private Distribution edgesLocalityDistribution;
    private double selfLoopValue;
    private long seed;

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

    public double getSelfLoopValue() {
        return selfLoopValue;
    }

    public void setSelfLoopValue(double selfLoopValue) {
        this.selfLoopValue = selfLoopValue;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
