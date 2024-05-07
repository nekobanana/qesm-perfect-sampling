package org.example.model.generator.distribution;

import org.example.model.utils.RandomUtils;

import java.util.Random;

public class UniformDistribution implements Distribution {
    private final Random random = RandomUtils.rand;
    final int min;
    final int max; // incluso
    public UniformDistribution(int min, int max) {
        this.min = min;
        this.max = max;
    }
    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @Override
    public int getSample() {
        return random.nextInt(max + 1 - min) + min;
    }

    int getIntervalLength() {
        return max - min + 1;
    }
}
