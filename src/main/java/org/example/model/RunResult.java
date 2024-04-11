package org.example.model;

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

    void setSampledState(int sampledState) {
        this.sampledState = sampledState;
    }

    void setSteps(int steps) {
        this.steps = steps;
    }
}