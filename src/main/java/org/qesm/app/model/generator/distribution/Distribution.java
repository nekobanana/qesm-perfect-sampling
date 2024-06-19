package org.qesm.app.model.generator.distribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = DistributionDeserializer.class)
public interface Distribution {
    void setSeed(long seed);
    @JsonIgnore
    int getSample();
    @JsonIgnore
    int getMin();
    @JsonIgnore
    int getMax();
    @JsonIgnore
    default int getIntervalLength() {
        return getMax() - getMin() + 1;
    }
    default Class getDistributionType() {
        return this.getClass();
    }
}
