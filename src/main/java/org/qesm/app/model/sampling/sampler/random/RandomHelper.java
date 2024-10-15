package org.qesm.app.model.sampling.sampler.random;

import java.util.Random;

public abstract class RandomHelper {

    protected Random rand;
    protected int n;

    public RandomHelper(Random rand, int n) {
        this.rand = rand;
        this.n = n;
    }

    public abstract double getRandomDouble();
    public abstract int getRandomInt();
    public abstract void init();
}
