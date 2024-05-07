package org.example.model.generator.distribution;

import org.example.model.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ManualDistribution implements Distribution {
    private final Random rand = RandomUtils.rand;
    private final List<UniformDistribution> distributions;
    private final List<Double> distributionsProbabilities;

    private ManualDistribution(List<UniformDistribution> distributions, List<Double> distributionsProbabilities) {
        this.distributions = distributions.stream()
                .map(d -> new UniformDistribution(d.min, d.max)).collect(Collectors.toList());
        this.distributionsProbabilities = new ArrayList<>(distributionsProbabilities);
    }

    @Override
    public void setSeed(long seed) {
        rand.setSeed(seed);
    }

    @Override
    public int getSample() {
        int distributionIndex = RandomUtils.getValueFromDistribution(distributionsProbabilities);
        Distribution distribution = distributions.get(distributionIndex);
        return distribution.getSample();
    }

    public static class ManualDistributionBuilder {
        private List<UniformDistribution> distributions = new ArrayList<>();
        private List<Double> distributionsWeights = new ArrayList<>();
        private boolean validProbabilities = true;

        private ManualDistributionBuilder() {}

        public static ManualDistributionBuilder newBuilder() {
            return new ManualDistributionBuilder();
        }

        public ManualDistributionBuilder distribution(UniformDistribution distribution, double weight) {
            distributions.add(distribution);
            distributionsWeights.add(weight);
            return this;
        }

        public ManualDistributionBuilder distribution(UniformDistribution distribution) {
            distributions.add(distribution);
            validProbabilities = false;
            return this;
        }

        public ManualDistribution build() {
            if (validProbabilities) {
                double sum = distributionsWeights.stream().mapToDouble(Double::doubleValue).sum();
                List<Double> distributionProbNormalized = distributionsWeights.stream()
                        .map(p -> p / sum).collect(Collectors.toList());
                return new ManualDistribution(distributions, distributionProbNormalized);
            }
            throw new RuntimeException("A weight must be provided for every distribution");
        }

        public ManualDistribution buildWithEqualProbabilities() {
            return new ManualDistribution(distributions, Arrays.asList(new Double[1 / distributions.size()]));
        }
    }
}
