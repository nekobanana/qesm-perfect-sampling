package org.qesm.app.model.sampling.sampler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Sampler {
    protected final int n;
    protected final Matrix P;
    @JsonIgnore
    protected Random rand;
    @JsonIgnore
    protected ObjectMapper mapper;

    public Sampler(Matrix P) {
        assert (P.rows() == P.columns());
        this.n = P.rows();
        this.P = P;
        this.rand = RandomUtils.rand;
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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

    public int getN() {return n;}

    abstract public void reset();
}
