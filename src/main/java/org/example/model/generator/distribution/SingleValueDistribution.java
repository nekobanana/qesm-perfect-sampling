package org.example.model.generator.distribution;

public class SingleValueDistribution extends UniformDistribution {
    private final int n;
    public SingleValueDistribution(int n) {
        super(n, n);
        this.n = n;
    }
    @Override
    public void setSeed(long seed) {}

    @Override
    public int getSample() {
        return n;
    }
}
