package org.qesm.app.model;

import java.util.ArrayList;
import java.util.List;

public class Output {
    private String fileName;
    private Config config;
    private PerfectSamplingOutput perfectSamplingOutput;
    private List<DumbSamplingOutput> dumbSamplingOutputs = new ArrayList<>();

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setPerfectSamplingOutput(PerfectSamplingOutput perfectSamplingOutput) {
        this.perfectSamplingOutput = perfectSamplingOutput;
    }

    public List<DumbSamplingOutput> getDumbSamplingOutputs() {
        return dumbSamplingOutputs;
    }

    public void setDumbSamplingOutputs(List<DumbSamplingOutput> dumbSamplingOutputs) {
        this.dumbSamplingOutputs = dumbSamplingOutputs;
    }

    public Config getConfig() {
        return config;
    }

    public PerfectSamplingOutput getPerfectSamplingOutput() {
        return perfectSamplingOutput;
    }

    public static class PerfectSamplingOutput {
        Float avgSteps;
        Double sigma;
        Double distance;

        public void setAvgSteps(Float avgSteps) {
            this.avgSteps = avgSteps;
        }

        public void setSigma(Double sigma) {
            this.sigma = sigma;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Float getAvgSteps() {
            return avgSteps;
        }

        public Double getSigma() {
            return sigma;
        }

        public Double getDistance() {
            return distance;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static class DumbSamplingOutput {
        Integer sigmas;
        Integer steps;
        Double distance;

        public void setSigmas(Integer sigmas) {
            this.sigmas = sigmas;
        }

        public void setSteps(Integer steps) {
            this.steps = steps;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Integer getSigmas() {
            return sigmas;
        }

        public Integer getSteps() {
            return steps;
        }

        public Double getDistance() {
            return distance;
        }
    }
}
