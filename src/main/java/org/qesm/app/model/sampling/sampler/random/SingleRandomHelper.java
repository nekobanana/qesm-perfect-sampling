package org.qesm.app.model.sampling.sampler.random;

import java.util.Random;

public class SingleRandomHelper extends RandomHelper {

    private double currentDouble;
    private int currentInt;

    public SingleRandomHelper(Random rand, int n) {
        super(rand, n);
    }

    @Override
    public double getRandomDouble() {
        return currentDouble;
    }

    @Override
    public int getRandomInt() {
        return currentInt;
    }

    @Override
    public void init() {
        this.currentDouble = rand.nextDouble();
        this.currentInt = rand.nextInt(n);
    }
}
