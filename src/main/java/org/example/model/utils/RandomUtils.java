package org.example.model.utils;

import java.util.List;
import java.util.Random;

public class RandomUtils {
    public static int getValueFromDistribution(List<Double> distribution) {
        double leftThreshold;
        double rightThreshold = 0;
        double r = rand.nextFloat();
        for (int j = 0; j < distribution.size(); j++) {
            leftThreshold = rightThreshold;
            rightThreshold = distribution.get(j) + leftThreshold;
            if (r < rightThreshold) {
                return j;
            }
        }
        throw new RuntimeException("Error generating random number");
    }

    public static final Random rand = new RandomLog();
    public static void setSeed(long seed) {
        rand.setSeed(seed);
    }

    public static class RandomLog extends Random {
        public RandomLog() {
            super();
        }
        public RandomLog(long seed) {
            super(seed);
        }

        @Override
        public int nextInt() {
            int v = super.nextInt();
            Log.logger.info(this + " nextInt() = " + v);
            return v;
        }
        @Override
        public long nextLong() {
            long v = super.nextLong();
            Log.logger.info(this + " nextLong() = " + v);
            return v;
        }
        @Override
        public double nextDouble() {
            double v = super.nextDouble();
            Log.logger.info(this + " nextDouble() = " + v);
            return v;
        }
        @Override
        public float nextFloat() {
            float v = super.nextFloat();
            Log.logger.info(this + " nextFloat() = " + v);
            return v;
        }
    }
}
