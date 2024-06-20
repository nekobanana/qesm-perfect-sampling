package org.qesm.app.model.generator.distribution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SingleValueDistribution.class)
public class SingleValueDistribution extends UniformDistribution {
    private final int n;

    @JsonCreator
    public SingleValueDistribution(@JsonProperty("n") int n) {
        super(n, n);
        this.n = n;
    }
    @Override
    public void setSeed(long seed) {}

    @Override
    public int getSample() {
        return n;
    }

    @JsonIgnore
    @Override
    public int getMin() {
        return n;
    }

    @JsonIgnore
    @Override
    public int getMax() {
        return n;
    }

    public int getN() {
        return n;
    }
}
