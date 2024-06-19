package org.qesm.app.model.generator.distribution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.qesm.app.model.utils.RandomUtils;

import java.util.Random;

@JsonDeserialize(as = UniformDistribution.class)
public class UniformDistribution implements Distribution {
    private final Random random = RandomUtils.rand;
    final int min;
    final int max; // incluso

    /**
     * @param min included
     * @param max included
     */
    @JsonCreator
    public UniformDistribution(@JsonProperty("min") int min, @JsonProperty("max") int max) {
        this.min = min;
        this.max = max;
    }
    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @JsonIgnore
    @Override
    public int getSample() {
        return random.nextInt(max + 1 - min) + min;
    }

    @Override
    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
