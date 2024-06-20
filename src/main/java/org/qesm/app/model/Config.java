package org.qesm.app.model;

import org.qesm.app.model.generator.distribution.Distribution;

public class Config {
    private int N;
    private Distribution edgesNumberDistribution;
    private Distribution edgesLocalityDistribution;
    private double selfLoopValue;
    private Long seed;
    private int run;
    private boolean connectSCCs;
    private String description;

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

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
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

    public void setDescription(String description) {
        this.description = description;
    }
}
