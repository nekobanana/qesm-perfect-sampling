package org.qesm.app.model.sampling.sampler.random;

import java.util.Random;

public class NRandomHelper extends RandomHelper {

    public NRandomHelper(Random rand, Integer n) {
        super(rand, n);
    }

    @Override
    public double getRandomDouble() {
        return rand.nextDouble();
    }

    @Override
    public int getRandomInt() {
        return rand.nextInt(n);
    }

    @Override
    public void init() {}
}
