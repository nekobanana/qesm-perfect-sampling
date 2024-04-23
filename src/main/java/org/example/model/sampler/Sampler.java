package org.example.model.sampler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.la4j.Matrix;

public abstract class Sampler {
    protected final int n;
    protected final Matrix P;
    @JsonIgnore
    protected ObjectMapper mapper;

    public Sampler(Matrix P) {
        assert (P.rows() == P.columns());

        this.n = P.rows();
        this.P = P;

        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    protected int generateNextStateNumber(int i) {
        double leftThreshold;
        double rightThreshold = 0;
        double r = Math.random();
        for (int j = 0; j < n; j++) {
            leftThreshold = rightThreshold;
            rightThreshold = P.get(i, j) + leftThreshold;
            if (r < rightThreshold) {
                return j;
            }
        }
        throw new RuntimeException("Error generating next state number");
    }

    abstract public void reset();
}
