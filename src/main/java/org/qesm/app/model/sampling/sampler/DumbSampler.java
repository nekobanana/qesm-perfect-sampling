package org.qesm.app.model.sampling.sampler;

import org.la4j.Matrix;
import java.util.ArrayList;
import java.util.List;

public class DumbSampler extends Sampler {

    private final List<Integer> sequence = new ArrayList<>();

    public DumbSampler(Matrix P) {
        super(P);
    }


    @Override
    public void reset() {
        sequence.clear();
    }

    public Integer runForNSteps(int initialState, int nSteps) {
        sequence.add(initialState);
        for (int i = 0; i < nSteps; i++) {
            double random = rand.nextDouble();
            int nextState = generateNextStateNumberFromRandomValue(sequence.get(i), random);
            sequence.add(nextState);
        }
        return sequence.get(sequence.size() - 1);
    }

}
