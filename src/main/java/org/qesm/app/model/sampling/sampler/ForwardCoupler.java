package org.qesm.app.model.sampling.sampler;

import org.la4j.Matrix;
import org.qesm.app.model.utils.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ForwardCoupler extends Sampler {
    int currentTime = 0;

    public ForwardCoupler(Matrix P) {
        super(P);
    }

    @Override
    public void reset() {
        currentTime = 0;
    }

    public RunResult runUntilCoalescence(int startState1, int startState2) {
//        for (int[] pair: new Combinations(n, 2)) {
//        int currentState1 = pair[0];
//        int currentState2 = pair[1];
        int currentState1 = startState1;
        int currentState2 = startState2;
        while (currentState1 != currentState2) {
            currentTime++;
            List<Double> minProbabilities = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                minProbabilities.set(i, Math.min(P.get(currentState1, i), P.get(currentState2, i)));
            }
            double random = rand.nextDouble();
            if (random < minProbabilities.stream().mapToDouble(p -> p).sum()) {
                int commonState = RandomUtils.getValueFromDistribution(minProbabilities);
                currentState1 = commonState;
                currentState2 = commonState;
            } else {
                currentState1 = RandomUtils.getValueFromDistribution(
                        Arrays.stream(P.getRow(currentState1).toDenseVector().toArray()).boxed().collect(Collectors.toList()));
                currentState2 = RandomUtils.getValueFromDistribution(
                        Arrays.stream(P.getRow(currentState2).toDenseVector().toArray()).boxed().collect(Collectors.toList()));
            }
        }
//        }
        return new RunResult(currentState1, currentTime);
    }


    protected int generateNextStateNumberFromRandomValue(int i, double random) {
        double leftThreshold;
        double rightThreshold = 0;
        for (int j = 0; j < n; j++) {
            leftThreshold = rightThreshold;
            rightThreshold = P.get(i, j) + leftThreshold;
            if (random < rightThreshold) {
                return j;
            }
        }
        try {
            return RandomUtils.getValueFromDistribution(
                    Arrays.stream(P.getRow(i).toDenseVector().toArray()).boxed().collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Error generating next state number");
        }
    }
}
