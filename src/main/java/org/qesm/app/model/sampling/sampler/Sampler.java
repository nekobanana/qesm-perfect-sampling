package org.qesm.app.model.sampling.sampler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qesm.app.model.sampling.aliasmethod.AliasTable;
import org.qesm.app.model.utils.RandomUtils;
import org.la4j.Matrix;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Sampler {
    protected final int n;
    protected final Matrix P;
    protected AliasTable[] aliasTables;
    @JsonIgnore
    protected Random rand;
    @JsonIgnore
    protected ObjectMapper mapper;

    public Sampler(Matrix P) {
        assert (P.rows() == P.columns());
        this.n = P.rows();
        this.P = P;
        this.rand = RandomUtils.rand;
        this.aliasTables = new AliasTable[n];
        setupAliasTables();
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private void setupAliasTables() {
        double[] pRow = new double[n*n];
        for (int i = 0; i < n*n; i++) {
            for (int state1 = 0; state1 < n; state1++) {
                double p = 1;
                for (int state2 = 0; state2 < n; state2++) {
                    p *= P.get(state2, state1);
                }
        }

//            aliasTables[state] = new AliasTable(P.getRow(state).toDenseVector().toArray());
        }
    }

    protected int generateNextStateNumber(int state, int randomInt, double randomDouble) {
        return aliasTables[state].sample(randomInt, randomDouble);
    }

    public int getN() {return n;}

    abstract public void reset();
}
