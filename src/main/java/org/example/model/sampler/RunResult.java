package org.example.model.sampler;

public class RunResult {
    int sampledState;
    int steps;

    public RunResult(int sampledState, int steps) {
        this.sampledState = sampledState;
        this.steps = steps;
    }

    public int getSampledState() {
        return sampledState;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "sampled state: " + sampledState + ", steps: " + steps;
    }
}