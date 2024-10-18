package org.qesm.app.model;

import org.la4j.Matrix;
import org.qesm.app.model.test.StatisticalTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Output {
    private String fileName;
    private Config config;
    private SteadyStateAnalysisOutput steadyStateAnalysisOutput;
    private PerfectSamplingOutput perfectSamplingOutput;
    private List<DumbSamplingOutput> dumbSamplingOutputs = new ArrayList<>();
    private DTMCGeneratorOutput dtmcGeneratorOutput;

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

    public DTMCGeneratorOutput getDtmcGeneratorOutput() {
        return dtmcGeneratorOutput;
    }

    public void setDtmcGeneratorOutput(DTMCGeneratorOutput dtmcGeneratorOutput) {
        this.dtmcGeneratorOutput = dtmcGeneratorOutput;
    }

    public SteadyStateAnalysisOutput getSteadyStateAnalysisOutput() {
        return steadyStateAnalysisOutput;
    }

    public void setSteadyStateAnalysisOutput(SteadyStateAnalysisOutput steadyStateAnalysisOutput) {
        this.steadyStateAnalysisOutput = steadyStateAnalysisOutput;
    }

    public static class DTMCGeneratorOutput {
        Matrix P;

        public Matrix getP() {
            return P;
        }

        public void setP(Matrix p) {
            P = p;
        }
    }

    public static class PerfectSamplingOutput {
        Float avgSteps;
        Double sigma;
        Double distance;
        StatisticalTest statisticalTest;

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

        public StatisticalTest getStatisticalTest() {
            return statisticalTest;
        }

        public void setStatisticalTest(StatisticalTest statisticalTest) {
            this.statisticalTest = statisticalTest;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static class DumbSamplingOutput {
        Double sigmas;
        Integer steps;
        Double distance;

        public void setSigmas(Double sigmas) {
            this.sigmas = sigmas;
        }

        public void setSteps(Integer steps) {
            this.steps = steps;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getSigmas() {
            return sigmas;
        }

        public Integer getSteps() {
            return steps;
        }

        public Double getDistance() {
            return distance;
        }
    }

    public static class SteadyStateAnalysisOutput {
        Map<Integer, Double> steadyStateDistribution;

        public Map<Integer, Double> getSteadyStateDistribution() {
            return steadyStateDistribution;
        }

        public void setSteadyStateDistribution(Map<Integer, Double> steadyStateDistribution) {
            this.steadyStateDistribution = steadyStateDistribution;
        }
    }
}
