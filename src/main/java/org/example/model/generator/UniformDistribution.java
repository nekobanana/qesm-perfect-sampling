package org.example.model.generator;

import java.util.Random;

public class UniformDistribution implements Distribution {
    Random random = new Random();
    int min;
    int max;
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
        return (int)(random.nextFloat() * (max - min) + min);
    }
}
