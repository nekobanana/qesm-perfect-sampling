package org.example.model.generator.distribution;

public interface Distribution {
    void setSeed(long seed);
    int getSample();
}
