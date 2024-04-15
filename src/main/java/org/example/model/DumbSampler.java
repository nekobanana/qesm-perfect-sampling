package org.example.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            int nextState = generateNextStateNumber(sequence.get(i));
            sequence.add(nextState);
        }
        return sequence.get(sequence.size() - 1);
    }

}
