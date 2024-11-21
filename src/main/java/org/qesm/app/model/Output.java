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
    private List<DumbSamplingOutputPS> dumbSamplingOutputsPS = new ArrayList<>();
    private List<DumbSamplingOutputFC> dumbSamplingOutputsFC = new ArrayList<>();
    private ForwardSamplingOutput forwardSamplingOutput;
    private ForwardCouplingOutput forwardCouplingOutput;
    private DTMCGeneratorOutput dtmcGeneratorOutput;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setPerfectSamplingOutput(PerfectSamplingOutput perfectSamplingOutput) {
        this.perfectSamplingOutput = perfectSamplingOutput;
    }

    public List<DumbSamplingOutputPS> getDumbSamplingOutputsPS() {
        return dumbSamplingOutputsPS;
    }

    public void setDumbSamplingOutputsPS(List<DumbSamplingOutputPS> dumbSamplingOutputsPS) {
        this.dumbSamplingOutputsPS = dumbSamplingOutputsPS;
    }

    public List<DumbSamplingOutputFC> getDumbSamplingOutputsFC() {
        return dumbSamplingOutputsFC;
    }

    public void setDumbSamplingOutputsFC(List<DumbSamplingOutputFC> dumbSamplingOutputsFC) {
        this.dumbSamplingOutputsFC = dumbSamplingOutputsFC;
    }

    public ForwardSamplingOutput getForwardSamplingOutput() {
        return forwardSamplingOutput;
    }

    public void setForwardSamplingOutput(ForwardSamplingOutput forwardSamplingOutput) {
        this.forwardSamplingOutput = forwardSamplingOutput;
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

    public ForwardCouplingOutput getForwardCouplingOutput() {
        return forwardCouplingOutput;
    }

    public void setForwardCouplingOutput(ForwardCouplingOutput forwardCouplingOutput) {
        this.forwardCouplingOutput = forwardCouplingOutput;
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
        Double l2DividedByNDistance;
        Double totalVariationDistance;
        StatisticalTest statisticalTest;

        public void setAvgSteps(Float avgSteps) {
            this.avgSteps = avgSteps;
        }

        public void setSigma(Double sigma) {
            this.sigma = sigma;
        }

        public void setL2DividedByNDistance(Double l2DividedByNDistance) {
            this.l2DividedByNDistance = l2DividedByNDistance;
        }

        public Double getTotalVariationDistance() {
            return totalVariationDistance;
        }

        public void setTotalVariationDistance(Double totalVariationDistance) {
            this.totalVariationDistance = totalVariationDistance;
        }

        public Float getAvgSteps() {
            return avgSteps;
        }

        public Double getSigma() {
            return sigma;
        }

        public Double getL2DividedByNDistance() {
            return l2DividedByNDistance;
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

    public static abstract class DumbSamplingOutput {
        Integer steps;
        Double l2DividedByNDistance;
        Double totalVariationDistance;
        String description;

        public void setSteps(Integer steps) {
            this.steps = steps;
        }

        public void setL2DividedByNDistance(Double l2DividedByNDistance) {
            this.l2DividedByNDistance = l2DividedByNDistance;
        }


        public Integer getSteps() {
            return steps;
        }

        public Double getL2DividedByNDistance() {
            return l2DividedByNDistance;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getTotalVariationDistance() {
            return totalVariationDistance;
        }

        public void setTotalVariationDistance(Double totalVariationDistance) {
            this.totalVariationDistance = totalVariationDistance;
        }
    }
    public static class DumbSamplingOutputPS extends DumbSamplingOutput {
        Double sigma;

        public Double getSigma() {
            return sigma;
        }

        public void setSigma(Double sigma) {
            this.sigma = sigma;
        }
    }
    public static class DumbSamplingOutputFC extends DumbSamplingOutput {
        Integer quantile;

        public Integer getQuantile() {
            return quantile;
        }

        public void setQuantile(Integer quantile) {
            this.quantile = quantile;
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

    public static class ForwardSamplingOutput {
        Float avgSteps;
        Double sigma;

        public Float getAvgSteps() {
            return avgSteps;
        }

        public void setAvgSteps(Float avgSteps) {
            this.avgSteps = avgSteps;
        }

        public Double getSigma() {
            return sigma;
        }

        public void setSigma(Double sigma) {
            this.sigma = sigma;
        }
    }

    public static class ForwardCouplingOutput {
        Float avgSteps;
        Double sigma;
        Double[] quantiles = new Double[101];

        public Float getAvgSteps() {
            return avgSteps;
        }

        public void setAvgSteps(Float avgSteps) {
            this.avgSteps = avgSteps;
        }

        public Double getSigma() {
            return sigma;
        }

        public void setSigma(Double sigma) {
            this.sigma = sigma;
        }

        public Double[] getQuantiles() {
            return quantiles;
        }

        public void setQuantiles(Double[] quantiles) {
            this.quantiles = quantiles;
        }
    }


}
