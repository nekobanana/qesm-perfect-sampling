package org.example.model.generator.distribution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = DistributionDeserializer.class)
public interface Distribution {
    void setSeed(long seed);
    int getSample();
    default Class getDistributionType() {
        return this.getClass();
    }
}
