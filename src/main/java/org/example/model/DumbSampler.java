package org.example.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.la4j.Matrix;
import java.util.ArrayList;
import java.util.List;

public class DumbSampler extends Sampler {
//    private final int n;
//    private final Matrix P;
    private Integer initialState;
    private Integer nSteps;
    private final List<Integer> sequence = new ArrayList<>();
//    @JsonIgnore
//    private ObjectMapper mapper;

    public DumbSampler(Matrix P) {
        super(P);
//        assert (P.rows() == P.columns());
//
//        this.n = P.rows();
//        this.P = P;
//
//        mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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
